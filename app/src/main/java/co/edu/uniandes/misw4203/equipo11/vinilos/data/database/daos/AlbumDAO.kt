package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
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
    suspend fun deleteAndInsertAlbums(albums: List<Album>) {
        deleteAlbums()
        insertAlbums(albums)
    }
}
