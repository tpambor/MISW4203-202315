package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformerDAO {
    @Query("SELECT * FROM performer WHERE type = :performerType ORDER BY name COLLATE UNICODE")
    fun getPerformersByType(performerType: PerformerType): Flow<List<Performer>>

    fun getMusicians(): Flow<List<Performer>> = getPerformersByType(PerformerType.MUSICIAN)

    fun getBands(): Flow<List<Performer>> = getPerformersByType(PerformerType.BAND)

    @Query("SELECT p.* FROM CollectorFavoritePerformer cp JOIN Performer p on cp.performerId = p.id WHERE cp.collectorId = :collectorId")
    fun getFavoritePerformersByCollectorId(collectorId: Int): Flow<List<Performer>>

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
