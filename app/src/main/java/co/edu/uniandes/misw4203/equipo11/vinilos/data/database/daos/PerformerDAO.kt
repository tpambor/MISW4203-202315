package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorFavoritePerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toCollector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toPerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
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

    // Internal use only
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectors(collectors: List<Collector>)

    // Internal use only
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectorFavoritePerformers(collectorFavoritePerformers: List<CollectorFavoritePerformer>)

    @Transaction
    @Query("DELETE FROM CollectorFavoritePerformer WHERE performerId IN (SELECT id FROM Performer WHERE Performer.type = :performerType)")
    suspend fun deleteCollectorFavoritePerformerByPerformerType(performerType: PerformerType)

    // Refresh the list of musicians and their collectors
    // To make sure that the database is consistent it is necessary to update the collectors
    // associated with the musicians as well
    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<MusicianJson>) {
        val collectors: MutableList<Collector> = mutableListOf()
        val collectorFavoritePerformers: MutableList<CollectorFavoritePerformer> = mutableListOf()
        val mappedMusicians = musicians.map { musician ->
            val favoriteCollectors: List<Collector> = requireNotNull(musician.collectors).map { it.toCollector() }
            collectors.addAll(favoriteCollectors)
            favoriteCollectors.forEach { favCollector ->
                collectorFavoritePerformers.add(
                    CollectorFavoritePerformer(favCollector.id, musician.id)
                )
            }

            musician.toPerformer()
        }

        deletePerformersByType(PerformerType.MUSICIAN)
        insertPerformers(mappedMusicians)
        deleteCollectorFavoritePerformerByPerformerType(PerformerType.MUSICIAN)
        insertCollectorFavoritePerformers(collectorFavoritePerformers)
        insertCollectors(collectors)
    }

    // Refresh the list of bands and their collectors
    // To make sure that the database is consistent it is necessary to update the collectors
    // associated with the bands as well
    @Transaction
    suspend fun deleteAndInsertBands(musicians: List<BandJson>) {
        val collectors: MutableList<Collector> = mutableListOf()
        val collectorFavoritePerformers: MutableList<CollectorFavoritePerformer> = mutableListOf()
        val mappedBands = musicians.map { band ->
            val favoriteCollectors: List<Collector> = requireNotNull(band.collectors).map { it.toCollector() }
            collectors.addAll(favoriteCollectors)
            favoriteCollectors.forEach { favCollector ->
                collectorFavoritePerformers.add(
                    CollectorFavoritePerformer(favCollector.id, band.id)
                )
            }

            band.toPerformer()
        }

        deletePerformersByType(PerformerType.BAND)
        insertPerformers(mappedBands)
        deleteCollectorFavoritePerformerByPerformerType(PerformerType.BAND)
        insertCollectorFavoritePerformers(collectorFavoritePerformers)
        insertCollectors(collectors)
    }
}
