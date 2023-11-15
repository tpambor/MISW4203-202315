package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJson
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDAO {
    @Query("SELECT * FROM album ORDER BY name COLLATE UNICODE")
    fun getAlbums(): Flow<List<Album>>

    @Insert
    suspend fun insertAlbums(albums: List<Album>)

    @Query("DELETE FROM album")
    suspend fun deleteAlbums()

    @Transaction
    suspend fun deleteAndInsertAlbums(albums: List<AlbumJson>) {
        val mappedAlbums = albums.map { it.toAlbum() }

        deleteAlbums()
        insertAlbums(mappedAlbums)
    }
    @Query("SELECT * FROM album WHERE id = :albumId")
    fun getAlbumById(albumId: Int): Flow<Album?>

    @Query("SELECT pe.* FROM PerformerAlbum pa JOIN Album a on pa.albumId = a.id JOIN performer pe on pe.id=pa.performerId WHERE pa.albumId == :albumId ORDER BY name COLLATE UNICODE")
    fun getPerformersByAlbumId(albumId: Int): Flow<List<Performer>>

    @Query("SELECT pe.* FROM CommentAlbum pa JOIN Album a on pa.albumId = a.id JOIN Comment pe on pe.id=pa.commentId WHERE pa.albumId == :albumId ORDER BY name COLLATE UNICODE")
    fun getCommentsByAlbumId(albumId: Int): Flow<List<Comment>>

    @Query("SELECT pe.* FROM TrackAlbum pa JOIN Album a on pa.albumId = a.id JOIN Track pe on pe.id=pa.trackId WHERE pa.albumId == :albumId ORDER BY name COLLATE UNICODE")
    fun getTracksByAlbumId(albumId: Int): Flow<List<Track>>
}
