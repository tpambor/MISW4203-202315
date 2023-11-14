package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

interface IAlbumRepository {
    fun getAlbums(): Flow<List<Album>?>
    suspend fun refresh(): Boolean
    fun getAlbum(albumId: Int): Flow<Album?>
    fun getPerformanceAlbums(albumId: Int): Flow<List<Performer>>
    fun getCommentsAlbums(albumId: Int): Flow<List<Comment>>
    fun getTracksAlbums(albumId: Int): Flow<List<Track>>
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

    override fun getAlbum(albumId: Int): Flow<Album?> = flow {
        db.albumDao().getAlbumById(albumId).collect { album ->
            emit(album)
        }
    }

    override fun getPerformanceAlbums(albumId: Int): Flow<List<Performer>> = flow {
        db.albumDao().getPerformersByAlbumId(albumId).collect { performers ->
            emit(performers)
        }
    }

    override fun getCommentsAlbums(albumId: Int): Flow<List<Comment>> = flow {
        db.albumDao().getCommentsByAlbumId(albumId).collect { comments ->
            emit(comments)
        }
    }

    override fun getTracksAlbums(albumId: Int): Flow<List<Track>> = flow {
        db.albumDao().getTracksByAlbumId(albumId).collect { tracks ->
            emit(tracks)
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
