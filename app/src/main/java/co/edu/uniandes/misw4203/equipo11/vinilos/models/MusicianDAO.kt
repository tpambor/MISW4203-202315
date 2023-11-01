package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicianDAO {
    @Query("SELECT * FROM musician")
    fun getMusicians(): Flow<List<Musician>>

    @Insert
    suspend fun insertMusicians(musicians: List<Musician>)

    @Query("DELETE FROM musician")
    suspend fun deleteMusicians()

    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<Musician>) {
        deleteMusicians()
        insertMusicians(musicians)
    }
}