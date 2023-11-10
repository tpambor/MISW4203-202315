package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface ICollectorRepository {
    fun getCollectors(): Flow<List<Collector>?>
    fun getCollectorsWithFavoritePerformers(): Flow<List<CollectorWithPerformers>?>
    suspend fun refresh(): Boolean
}

class CollectorRepository : ICollectorRepository {
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getCollectors(): Flow<List<Collector>?> = flow {
        db.collectorDao().getCollectors().collect { collectors ->
            if (collectors.isEmpty()) {
                if(!refresh()) {
                    emit(null)
                }
            } else {
                emit(collectors)
            }
        }
    }

    override fun getCollectorsWithFavoritePerformers(): Flow<List<CollectorWithPerformers>?> = flow {
        db.collectorDao().getCollectorsWithPerformers().collect { collectors ->
            if (collectors.isEmpty()) {
                if(!refresh()) {
                    emit(null)
                }
            } else {
                emit(collectors)
            }
        }
    }

    override suspend fun refresh(): Boolean {
        val collectors: List<CollectorJson>

        try {
            collectors = adapter.getCollectors().first()
        } catch (ex: Exception) {
            Log.e(TAG, "Error loading Collectors: $ex")
            return false
        }

        db.collectorDao().deleteAndInsertCollectors(collectors)
        return true
    }

    companion object {
        private val TAG = CollectorRepository::class.simpleName!!
    }
}
