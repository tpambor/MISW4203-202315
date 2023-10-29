package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDAO {
    @Query("SELECT * FROM album")
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
