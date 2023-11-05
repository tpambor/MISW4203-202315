package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface IAlbumRepository {
    fun getAlbums(): Flow<List<Album>?>
    suspend fun refresh(): Boolean
}

class AlbumRepository : IAlbumRepository {
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getAlbums(): Flow<List<Album>?> = flow {
        db.albumDao().getAlbums().collect { albums ->
            if (albums.isEmpty()) {
                if(!refresh()) {
                    emit(null)
                }
            } else {
                emit(albums)
            }
        }
    }

    override suspend fun refresh(): Boolean {
        val albums: List<Album>?

        try {
            albums = adapter.getAlbums().first()
        } catch (ex: Exception) {
            Log.e(TAG, "Error loading albums: $ex")
            return false
        }

        db.albumDao().deleteAndInsertAlbums(albums)
        return true
    }

    companion object {
        private val TAG = AlbumRepository::class.simpleName!!
    }
}
