package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Instant

interface ICollectorRepository {
    fun getCollectorsWithFavoritePerformers(): Flow<Result<List<CollectorWithPerformers>>>
    fun getCollector(collectorId: Int): Flow<Collector?>
    fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>>
    suspend fun refresh()
}

class CollectorRepository : ICollectorRepository {
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getCollectorsWithFavoritePerformers(): Flow<Result<List<CollectorWithPerformers>>> = flow {
        var isFirst = true

        db.collectorDao().getCollectorsWithPerformers().collect { collectors ->
            if (!isFirst)
                emit(Result.success(collectors))

            // Handle first list returned differently
            //
            // If the first list is empty, there is no data in the database.
            // This is mostly likely due to never have loaded data from the API,
            // therefore call refresh() in this case to load the data from the API.
            isFirst = true
            if(collectors.isNotEmpty()) {
                emit(Result.success(collectors))
            }
            else {
                try {
                    refresh()
                } catch (ex: Exception) {
                    Log.e(TAG, "Error loading albums: $ex")
                    emit(Result.failure(ex))
                }
            }
        }
    }

    override fun getCollector(collectorId: Int): Flow<Collector?> = flow {
        db.collectorDao().getCollectorById(collectorId).collect { collector ->
            emit(collector)
        }
    }

    override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> = flow {
        val performers = listOf(
            Performer(
                id = 1,
                PerformerType.BAND,
                name = "Alcolirycoz",
                "red",
                "Lorem ipsum",
                Instant.now()
            ),
            Performer(
                id = 2,
                PerformerType.MUSICIAN,
                name = "Rubén Blades",
                "blue",
                "Cantante salsa",
                Instant.now()
            )
        )

        emit(performers)
    }

    override suspend fun refresh() {
        db.collectorDao().deleteAndInsertCollectors(adapter.getCollectors().first())
    }

    companion object {
        private val TAG = CollectorRepository::class.simpleName!!
    }
}
