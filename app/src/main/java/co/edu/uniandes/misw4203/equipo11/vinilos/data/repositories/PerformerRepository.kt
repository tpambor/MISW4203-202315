package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories
import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


interface IPerformerRepository {
    fun getMusicians(): Flow<List<Performer>?>
    fun getBands(): Flow<List<Performer>?>
    fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>>
    fun getMusician(performerId: Int): Flow<Performer?>
    fun getAlbums(performerId: Int): Flow<List<Album>>
    suspend fun refreshMusicians(): Boolean
    suspend fun refreshBands(): Boolean
}

class PerformerRepository : IPerformerRepository{
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getMusicians(): Flow<List<Performer>?> = flow {
        db.performerDao().getMusicians().collect { musicians ->
            if (musicians.isEmpty()) {
                if(!refreshMusicians()) {
                    emit(null)
                }
            } else {
                emit(musicians)
            }
        }
    }

    override fun getBands(): Flow<List<Performer>?> = flow {
        db.performerDao().getBands().collect { bands ->
            if (bands.isEmpty()) {
                if(!refreshBands()) {
                    emit(null)
                }
            } else {
                emit(bands)
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

    override fun getAlbums(performerId: Int): Flow<List<Album>> = flow {
        db.albumDao().getAlbumsByPerformerId(performerId).collect { albums ->
            emit(albums)
        }
    }

    override suspend fun refreshMusicians(): Boolean {
        val musicians: List<MusicianJson>

        try {
            musicians = adapter.getMusicians().first()
        } catch (ex: Exception) {
            Log.e(TAG, "Error loading musicians: $ex")
            return false
        }
        db.performerDao().deleteAndInsertMusicians(musicians)
        return true
    }

    override suspend fun refreshBands(): Boolean {
        val bands: List<BandJson>

        try {
            bands = adapter.getBands().first()
        } catch (ex: Exception) {
            Log.e(TAG, "Error loading bands: $ex")
            return false
        }
        db.performerDao().deleteAndInsertBands(bands)
        return true
    }

    companion object {
        private val TAG = PerformerRepository::class.simpleName!!
    }
}
