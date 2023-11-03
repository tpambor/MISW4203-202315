package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BandDAO {
    @Query("SELECT * FROM performer WHERE type = :performerType")
    fun getBands(performerType: PerformerType = PerformerType.BAND): Flow<List<Performer>>

    @Insert
    suspend fun insertBands(bands: List<Performer>)

    @Query("DELETE FROM performer WHERE type = :performerType")
    suspend fun deleteBands(performerType: PerformerType = PerformerType.BAND)

    @Transaction
    suspend fun deleteAndInsertBands(bands: List<Performer>) {
        deleteBands()
        insertBands(bands)
    }
}