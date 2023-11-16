package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories
import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorFavoritePerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


interface IPerformerRepository {
    fun getMusicians(): Flow<Result<List<Performer>>>
    fun getBands(): Flow<Result<List<Performer>>>
    fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>>
    fun getMusician(performerId: Int): Flow<Performer?>
    fun getBand(performerId: Int): Flow<Performer?>
    fun getBandMembers(performerId: Int): Flow<List<Performer>>
    fun getAlbums(performerId: Int): Flow<List<Album>>
    suspend fun addFavoriteMusician(collectorId: Int, performerId: Int)
    suspend fun addFavoriteBand(collectorId: Int, performerId: Int)
    suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int)
    suspend fun removeFavoriteBand(collectorId: Int, performerId: Int)
    suspend fun refreshMusicians()
    suspend fun refreshBands()
}

class PerformerRepository : IPerformerRepository{
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getMusicians(): Flow<Result<List<Performer>>> = flow {
        var isFirst = true

        db.performerDao().getMusicians().collect { musicians ->
            if (!isFirst)
                emit(Result.success(musicians))

            // Handle first list returned differently
            //
            // If the first list is empty, there is no data in the database.
            // This is mostly likely due to never have loaded data from the API,
            // therefore call refresh() in this case to load the data from the API.
            isFirst = true
            if(musicians.isNotEmpty()) {
                emit(Result.success(musicians))
            }
            else {
                try {
                    refreshMusicians()
                } catch (ex: Exception) {
                    Log.e(TAG, "Error loading musicians: $ex")
                    emit(Result.failure(ex))
                }
            }
        }
    }

    override fun getBands(): Flow<Result<List<Performer>>> = flow {
        var isFirst = true

        db.performerDao().getBands().collect { bands ->
            if (!isFirst)
                emit(Result.success(bands))

            // Handle first list returned differently
            //
            // If the first list is empty, there is no data in the database.
            // This is mostly likely due to never have loaded data from the API,
            // therefore call refresh() in this case to load the data from the API.
            isFirst = true
            if(bands.isNotEmpty()) {
                emit(Result.success(bands))
            }
            else {
                try {
                    refreshBands()
                } catch (ex: Exception) {
                    Log.e(TAG, "Error loading bands: $ex")
                    emit(Result.failure(ex))
                }
            }
        }
    }

    override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> = flow {
        db.performerDao().getFavoritePerformersByCollectorId(collectorId).collect { performers ->
            emit(performers)
        }
    }

    override fun getMusician(performerId: Int): Flow<Performer?> = flow {
        db.performerDao().getMusicianById(performerId).collect { musician ->
            emit(musician)
        }
    }

    override fun getBand(performerId: Int): Flow<Performer?> = flow {
        db.performerDao().getBandById(performerId).collect { band ->
            emit(band)
        }
    }

    override fun getAlbums(performerId: Int): Flow<List<Album>> = flow {
        db.albumDao().getAlbumsByPerformerId(performerId).collect { albums ->
            emit(albums)
        }
    }

    override fun getBandMembers(performerId: Int): Flow<List<Performer>> = flow {
        db.performerDao().getBandMembers(performerId).collect() { musicians ->
            emit(musicians)
        }
    }

    override suspend fun addFavoriteMusician(collectorId: Int, performerId: Int) {
        adapter.addFavoriteMusicianToCollector(collectorId, performerId).first()

        db.collectorDao().insertCollectorFavoritePerformers(listOf(
            CollectorFavoritePerformer(collectorId, performerId)
        ))
    }

    override suspend fun addFavoriteBand(collectorId: Int, performerId: Int) {
        adapter.addFavoriteBandToCollector(collectorId, performerId).first()

        db.collectorDao().insertCollectorFavoritePerformers(listOf(
            CollectorFavoritePerformer(collectorId, performerId)
        ))
    }

    override suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int) {
        adapter.removeFavoriteMusicianFromCollector(collectorId, performerId).first()

        db.collectorDao().deleteCollectorFavoritePerformer(
            CollectorFavoritePerformer(collectorId, performerId)
        )
    }

    override suspend fun removeFavoriteBand(collectorId: Int, performerId: Int) {
        adapter.removeFavoriteBandFromCollector(collectorId, performerId).first()

        db.collectorDao().deleteCollectorFavoritePerformer(
            CollectorFavoritePerformer(collectorId, performerId)
        )
    }

    override suspend fun refreshMusicians() {
        db.performerDao().deleteAndInsertMusicians(
            adapter.getMusicians().first()
        )
    }

    override suspend fun refreshBands() {
        db.performerDao().deleteAndInsertBands(
            adapter.getBands().first()
        )
    }

    companion object {
        private val TAG = PerformerRepository::class.simpleName!!
    }
}
