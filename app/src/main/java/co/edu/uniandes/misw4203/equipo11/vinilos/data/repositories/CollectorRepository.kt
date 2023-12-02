package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Cache
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Instant

interface ICollectorRepository {
    fun getCollectorsWithFavoritePerformers(): Flow<List<CollectorWithPerformers>>
    fun getCollector(collectorId: Int): Flow<Collector?>
    fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>>
    fun getAlbums(collectorId: Int): Flow<List<CollectorAlbum>>
    suspend fun refresh()
    suspend fun needsRefresh(): Boolean
    suspend fun refreshCollector(collectorId: Int)
}

class CollectorRepository : ICollectorRepository {
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getCollectorsWithFavoritePerformers(): Flow<List<CollectorWithPerformers>> = flow {
        db.collectorDao().getCollectorsWithPerformers().collect { collectors ->
            emit(collectors)
        }
    }

    override fun getCollector(collectorId: Int): Flow<Collector?> = flow {
        db.collectorDao().getCollectorById(collectorId).collect { collector ->
            emit(collector)
        }
    }

    override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> = flow {
        db.collectorDao().getFavoritePerformers(collectorId).collect { performers ->
            emit(performers)
        }
    }
    override fun getAlbums(collectorId: Int): Flow<List<CollectorAlbum>> = flow {
        db.collectorDao().getAlbums(collectorId).collect { albums ->
            emit(albums)
        }
    }

    override suspend fun refresh() {
        db.collectorDao().deleteAndInsertCollectors(adapter.getCollectors().first())

        db.cacheDao().setLastUpdate(Cache("collectors", Instant.now()))
    }

    override suspend fun needsRefresh(): Boolean {
        val lastUpdate = db.cacheDao().getLastUpdate("collectors") ?: return true

        return Duration.between(lastUpdate, Instant.now()) > Duration.ofDays(1)
    }

    override suspend fun refreshCollector(collectorId: Int) {
        db.collectorDao().deleteAndInsertCollectors(
            listOf(adapter.getCollector(collectorId).first()),
            deleteAll = false
        )
    }
}
