package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicianDAO {
    @Query("SELECT * FROM performer WHERE type = :performerType")
    fun getMusicians(performerType: PerformerType = PerformerType.MUSICIAN): Flow<List<Performer>>
    @Insert
    suspend fun insertMusicians(musicians: List<Performer>)

    @Query("DELETE FROM performer WHERE type = :performerType")
    suspend fun deleteMusicians(performerType: PerformerType = PerformerType.MUSICIAN)

    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<Performer>) {
        deleteMusicians()
        insertMusicians(musicians)
    }
}