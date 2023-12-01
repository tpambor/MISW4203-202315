package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Cache
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toComment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toTrack
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Instant

interface IAlbumRepository {
    fun getAlbums(): Flow<List<Album>>
    suspend fun refresh()
    suspend fun needsRefresh(): Boolean
    fun getAlbum(albumId: Int): Flow<Album?>
    suspend fun addTrack(albumId: Int, name: String, duration: String)
    fun getPerformers(albumId: Int): Flow<List<Performer>>
    fun getComments(albumId: Int): Flow<List<Comment>>
    fun getTracks(albumId: Int): Flow<List<Track>>
    suspend fun refreshAlbum(albumId: Int)
    suspend fun insertAlbum(album: AlbumRequestJson)
    suspend fun addComment(albumId: Int, collectorId: Int, rating: Int, comment: String)
}

class AlbumRepository : IAlbumRepository {
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getAlbums(): Flow<List<Album>> = flow {
        db.albumDao().getAlbums().collect { albums ->
            emit(albums)
        }
    }

    override fun getAlbum(albumId: Int): Flow<Album?> = flow {
        db.albumDao().getAlbumById(albumId).collect { album ->
            emit(album)
        }
    }

    override suspend fun insertAlbum(album: AlbumRequestJson)
    {
        val newAlbum = adapter.insertAlbum(album).first()

        db.albumDao().insertAlbums(listOf(
            newAlbum.toAlbum()
        ))
    }

    override suspend fun addTrack(albumId: Int, name: String, duration: String) {
        db.albumDao().insertTracks(listOf(
            adapter.addTrackToAlbum(albumId, name, duration).first()
                .toTrack(albumId)
        ))
    }

    override fun getPerformers(albumId: Int): Flow<List<Performer>> = flow {
        db.albumDao().getPerformersByAlbumId(albumId).collect { performers ->
            emit(performers)
        }
    }

    override fun getComments(albumId: Int): Flow<List<Comment>> = flow {
        db.albumDao().getCommentsByAlbumId(albumId).collect { comments ->
            emit(comments)
        }
    }

    override fun getTracks(albumId: Int): Flow<List<Track>> = flow {
        db.albumDao().getTracksByAlbumId(albumId).collect { tracks ->
            emit(tracks)
        }
    }

    override suspend fun refresh() {
        db.albumDao().deleteAndInsertAlbums(
            adapter.getAlbums().first()
        )

        db.cacheDao().setLastUpdate(Cache("albums", Instant.now()))
    }

    override suspend fun needsRefresh(): Boolean {
        val lastUpdate = db.cacheDao().getLastUpdate("albums") ?: return true

        return Duration.between(lastUpdate, Instant.now()) > Duration.ofDays(1)
    }

    override suspend fun refreshAlbum(albumId: Int) {
        db.albumDao().deleteAndInsertAlbums(
            listOf(adapter.getAlbum(albumId).first()),
            deleteAll = false
        )
    }

    override suspend fun addComment(albumId: Int, collectorId: Int, rating: Int, comment: String) {
        db.albumDao().insertComments(listOf(
            adapter.addCommentToAlbum(albumId, collectorId, rating, comment).first()
                .toComment(albumId)
        ))
    }
}
