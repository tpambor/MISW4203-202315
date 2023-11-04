package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface CollectorDAO {
    @Query("SELECT * FROM collector")
    fun getCollectors(): Flow<List<Collector>>

    //
    // Query list of collectors together with their favorite performers
    //
    // The relationship between Collector and Performers is many-to-many and
    // cannot automatically be modelled by Room.
    // Therefore, the query is created here manually.
    // The query returns a row for each pair of Collector and Performer as such that
    // if a collector has various favorite performers various rows are returned.
    // If a collector has no favorite performers, null is returned.
    //
    // Example:
    //   - Collector C1 has favorite performs P1, P2
    //   - Collector C2 favorite performers P2
    //   - Collector C3 has no favorite performers
    //
    //  Rows returned by query:
    //   - C1, P1
    //   - C1, P2
    //   - C2, P2
    //   - C3, null

    // Internal only helper data class
    data class CollectorWithPerformer(
        @Embedded(prefix = "collector_") val collector: Collector,
        @Embedded(prefix = "performer_") val performer: Performer?
    )

    @Transaction
    @Query(
        "SELECT c.id as collector_id, c.name as collector_name, c.telephone as collector_telephone, c.email as collector_email, " +
        "p.id as performer_id, p.type as performer_type, p.name as performer_name, p.image as performer_image, p.description as performer_description, p.birthDate as performer_birthDate " +
        "FROM Collector c LEFT JOIN CollectorFavoritePerformer cp ON cp.collectorId = c.id LEFT JOIN " +
        "Performer p ON cp.performerId = p.id"
    )
    fun getCollectorsWithPerformer(): Flow<List<CollectorWithPerformer>>

    //
    // Query list of collectors together with their favorite performers
    //
    // Gives a list of favorite performers for each collector
    //
    // Example:
    //   - Collector C1 has favorite performs P1, P2
    //   - Collector C2 favorite performers P2
    //   - Collector C3 has no favorite performers
    //
    //  List items returned by query:
    //   - C1, [P1, P2]
    //   - C2, [P2]
    //   - C3, []
    fun getCollectorsWithPerformers(): Flow<List<CollectorWithPerformers>> {
        return getCollectorsWithPerformer().map { collectors ->
            collectors
                .groupBy({ it.collector }, { it.performer })
                .map { CollectorWithPerformers(it.key, it.value.filterNotNull()) }
        }
    }

    @Insert
    suspend fun insertCollectors(collectors: List<Collector>)

    @Query("DELETE FROM collector")
    suspend fun deleteCollectors()
    @Transaction
    suspend fun deleteAndInsertCollectors(collectors: List<Collector>) {
        deleteCollectors()
        insertCollectors(collectors)
    }
}
