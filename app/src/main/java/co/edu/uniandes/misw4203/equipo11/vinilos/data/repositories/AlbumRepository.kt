package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface IAlbumRepository {
    fun getAlbums(): Flow<Result<List<Album>>>
    suspend fun refresh()
}

class AlbumRepository : IAlbumRepository {
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getAlbums(): Flow<Result<List<Album>>> = flow {
        var isFirst = true

        db.albumDao().getAlbums().collect { albums ->
            if (!isFirst)
                emit(Result.success(albums))

            // Handle first list returned differently
            //
            // If the first list is empty, there is no data in the database.
            // This is mostly likely due to never have loaded data from the API,
            // therefore call refresh() in this case to load the data from the API.
            isFirst = true
            if(albums.isNotEmpty()) {
                emit(Result.success(albums))
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

    override suspend fun refresh() {
        db.albumDao().deleteAndInsertAlbums(
            adapter.getAlbums().first()
        )
    }

    companion object {
        private val TAG = AlbumRepository::class.simpleName!!
    }
}
