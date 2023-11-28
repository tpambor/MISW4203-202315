package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toComment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toPerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toTrack
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJson
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AlbumDAO {
    @Query("SELECT * FROM album ORDER BY name COLLATE UNICODE")
    abstract fun getAlbums(): Flow<List<Album>>

    @Query("SELECT a.* FROM PerformerAlbum pa JOIN Album a on pa.albumId = a.id WHERE pa.performerId == :performerId ORDER BY a.name COLLATE UNICODE")
    abstract fun getAlbumsByPerformerId(performerId: Int): Flow<List<Album>>

    @Query("SELECT * FROM album WHERE id = :albumId")
    abstract fun getAlbumById(albumId: Int): Flow<Album?>

    @Query("SELECT pe.* FROM PerformerAlbum pa JOIN Album a on pa.albumId = a.id JOIN performer pe on pe.id = pa.performerId WHERE pa.albumId == :albumId ORDER BY pe.name COLLATE UNICODE")
    abstract fun getPerformersByAlbumId(albumId: Int): Flow<List<Performer>>

    @Query("SELECT pe.* FROM Album a JOIN Comment pe on pe.albumId=a.id WHERE a.id == :albumId")
    abstract fun getCommentsByAlbumId(albumId: Int): Flow<List<Comment>>

    @Query("SELECT pe.* FROM Album a JOIN Track pe on pe.albumId=a.id WHERE a.id == :albumId")
    abstract fun getTracksByAlbumId(albumId: Int): Flow<List<Track>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertAlbums(albums: List<Album>)

    @Query("DELETE FROM Album")
    protected abstract suspend fun deleteAlbums()

    @Query("DELETE FROM Track")
    protected abstract suspend fun deleteTracks()

    @Query("DELETE FROM Track WHERE albumId = :albumId")
    protected abstract suspend fun deleteTracksByAlbumId(albumId: Int)

    @Insert
    protected abstract suspend fun insertTracks(tracks: List<Track>)

    @Query("DELETE FROM Comment")
    protected abstract suspend fun deleteComments()

    @Query("DELETE FROM Comment WHERE albumId = :albumId")
    protected abstract suspend fun deleteCommentsByAlbumId(albumId: Int)

    @Insert
    abstract suspend fun insertComments(comments: List<Comment>)

    @Query("DELETE FROM PerformerAlbum")
    protected abstract suspend fun deletePerformerAlbums()

    @Query("DELETE FROM PerformerAlbum WHERE albumId = :albumId")
    protected abstract suspend fun deletePerformerAlbumsByAlbumId(albumId: Int)

    @Insert
    protected abstract suspend fun insertPerformerAlbums(tracks: List<PerformerAlbum>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPerformers(tracks: List<Performer>)

    // Refresh the list of albums
    // To make sure that the database is consistent it is necessary to update the track, comments and artists
    // associated with the albums as well
    //
    // It is necessary to set deleteAll to remove albums from the local database that are no longer available upstream
    @Transaction
    open suspend fun deleteAndInsertAlbums(albums: List<AlbumJson>, deleteAll: Boolean = true) {
        val tracks: MutableList<Track> = mutableListOf()
        val comments: MutableList<Comment> = mutableListOf()

        val performers: MutableList<Performer> = mutableListOf()
        val performerAlbums: MutableList<PerformerAlbum> = mutableListOf()

        val mappedAlbums = albums.map { album ->
            val albumTracks = requireNotNull(album.tracks).map { it.toTrack(album.id) }
            tracks.addAll(albumTracks)

            val albumComments = requireNotNull(album.comments).map { it.toComment(album.id) }
            comments.addAll(albumComments)

            val albumPerformers = requireNotNull(album.performers).map { it.toPerformer() }
            performers.addAll(albumPerformers)
            albumPerformers.forEach { performer ->
                performerAlbums.add(PerformerAlbum(performer.id, album.id))
            }

            album.toAlbum()
        }

        if (deleteAll)
            deleteAlbums()
        insertAlbums(mappedAlbums)

        insertPerformers(performers)
        if (deleteAll)
            deletePerformerAlbums()
        else {
            mappedAlbums.forEach { album ->
                deletePerformerAlbumsByAlbumId(album.id)
            }
        }
        insertPerformerAlbums(performerAlbums)

        if (deleteAll)
            deleteTracks()
        else {
            mappedAlbums.forEach { album ->
                deleteTracksByAlbumId(album.id)
            }
        }
        insertTracks(tracks)

        if (deleteAll)
            deleteComments()
        else {
            mappedAlbums.forEach { album ->
                deleteCommentsByAlbumId(album.id)
            }
        }
        insertComments(comments)
    }
}
