package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toPerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformerDAO {
    @Query("SELECT * FROM performer WHERE type = :performerType ORDER BY name COLLATE UNICODE")
    fun getPerformersByType(performerType: PerformerType): Flow<List<Performer>>

    @Query("SELECT * FROM performer WHERE id = :performerId AND type = :performerType")
    fun getPerformersByTypeId(performerType: PerformerType, performerId: Int): Flow<Performer?>

    fun getMusicians(): Flow<List<Performer>> = getPerformersByType(PerformerType.MUSICIAN)

    fun getMusicianById(performerId: Int): Flow<Performer?> = getPerformersByTypeId(PerformerType.MUSICIAN, performerId)

    fun getBands(): Flow<List<Performer>> = getPerformersByType(PerformerType.BAND)

    @Query("SELECT p.* FROM CollectorFavoritePerformer cp JOIN Performer p on cp.performerId = p.id WHERE cp.collectorId = :collectorId")
    fun getFavoritePerformersByCollectorId(collectorId: Int): Flow<List<Performer>>

    @Insert
    suspend fun insertPerformers(musicians: List<Performer>)

    @Query("DELETE FROM performer WHERE type = :performerType")
    suspend fun deletePerformersByType(performerType: PerformerType)

    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<MusicianJson>) {
        val performers = musicians.map { it.toPerformer() }
        deletePerformersByType(PerformerType.MUSICIAN)
        insertPerformers(performers)
    }

    @Transaction
    suspend fun deleteAndInsertBands(musicians: List<BandJson>) {
        val performers = musicians.map { it.toPerformer() }
        deletePerformersByType(PerformerType.BAND)
        insertPerformers(performers)
    }
}
