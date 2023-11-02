package co.edu.uniandes.misw4203.equipo11.vinilos.repositories
import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Band
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Musician
import co.edu.uniandes.misw4203.equipo11.vinilos.models.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


interface IPerformerRepository {
    fun getMusicians(): Flow<List<Musician>?>
    fun getBands(): Flow<List<Band>?>
    suspend fun refreshMusicians(): Boolean
    suspend fun refreshBands(): Boolean
}

class PerformerRepository : IPerformerRepository{
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getMusicians(): Flow<List<Musician>?> = flow {
        db.musicianDao().getMusicians().collect { musicians ->
            if (musicians.isEmpty()) {
                Log.i("Musicians NOT DAO", musicians.toString())
                if(!refreshMusicians()) {
                    emit(null)
                }
            } else {
                Log.i("Musicians DAO", musicians.toString())
                emit(musicians)

            }
        }
    }

    override fun getBands(): Flow<List<Band>?> = flow {
        db.bandDao().getBands().collect { bands ->
            if (bands.isEmpty()) {
                if(!refreshBands()) {
                    emit(null)
                }
            } else {
                Log.i("Bands DAO", bands.toString())
                emit(bands)
            }
        }
    }

    override suspend fun refreshMusicians(): Boolean {
        val musicians: List<Musician>?

        try {
            musicians = adapter.getMusicians().first()
            Log.i("refreshMusicians", musicians.toString())
        } catch (ex: Exception) {
            Log.e(TAG, "Error loading musicians: $ex")
            return false
        }
        db.musicianDao().deleteAndInsertMusicians(musicians)
        return true
    }

    override suspend fun refreshBands(): Boolean {
        val bands: List<Band>?

        try {
            bands = adapter.getBands().first()
            Log.i("refreshBands", bands.toString())
        } catch (ex: Exception) {
            Log.e(TAG, "Error loading bands: $ex")
            return false
        }
        db.bandDao().deleteAndInsertBands(bands)
        return true
    }

    companion object {
        private val TAG = PerformerRepository::class.simpleName!!
    }
}
