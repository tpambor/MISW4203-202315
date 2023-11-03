package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformerDAO {
    @Query("SELECT * FROM performer WHERE type = :performerType")
    fun getPerformersByType(performerType: PerformerType): Flow<List<Performer>>

    fun getMusicians(): Flow<List<Performer>> = getPerformersByType(PerformerType.MUSICIAN)

    fun getBands(): Flow<List<Performer>> = getPerformersByType(PerformerType.BAND)

    @Insert
    suspend fun insertPerformers(musicians: List<Performer>)

    @Query("DELETE FROM performer WHERE type = :performerType")
    suspend fun deletePerformersByType(performerType: PerformerType)

    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<Performer>) {
        deletePerformersByType(PerformerType.MUSICIAN)
        insertPerformers(musicians)
    }

    @Transaction
    suspend fun deleteAndInsertBands(musicians: List<Performer>) {
        deletePerformersByType(PerformerType.BAND)
        insertPerformers(musicians)
    }
}
