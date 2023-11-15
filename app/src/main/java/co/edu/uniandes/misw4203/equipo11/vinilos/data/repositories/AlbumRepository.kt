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
    fun getAlbums(): Flow<Result<List<Album>>>
    suspend fun refresh()
    fun getAlbum(albumId: Int): Flow<Album?>
    fun getPerformanceAlbums(albumId: Int): Flow<List<Performer>>
    fun getCommentsAlbums(albumId: Int): Flow<List<Comment>>
    fun getTracksAlbums(albumId: Int): Flow<List<Track>>
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

    override suspend fun refresh() {
        db.albumDao().deleteAndInsertAlbums(
            adapter.getAlbums().first()
        )
    }

    companion object {
        private val TAG = AlbumRepository::class.simpleName!!
    }
}
