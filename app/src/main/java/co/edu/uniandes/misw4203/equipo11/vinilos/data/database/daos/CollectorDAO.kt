package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbumCrossRef
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorFavoritePerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toCollector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toCollectorAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toPerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.Normalizer

@Dao
abstract class CollectorDAO {
    @Query("SELECT * FROM collector WHERE id = :collectorId")
    abstract fun getCollectorById(collectorId: Int): Flow<Collector?>

    //
    // Query list of collectors together with their favorite performers
    //
    // The relationship between Collector and Performers is many-to-many and
    // cannot automatically be modelled by Room. Therefore, the query is created here manually.
    //
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
    protected data class CollectorWithPerformer(
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
    protected abstract fun getCollectorsWithPerformer(): Flow<List<CollectorWithPerformer>>

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
    open fun getCollectorsWithPerformers(): Flow<List<CollectorWithPerformers>> {
        return getCollectorsWithPerformer().map { collectors ->
            collectors
                .groupBy({ it.collector }, { it.performer })
                .map { collector ->
                    CollectorWithPerformers(
                        collector.key,
                        collector.value
                            .filterNotNull()
                            .sortedBy { Normalizer.normalize(it.name, Normalizer.Form.NFD)  }
                    )
                }
                .sortedBy { Normalizer.normalize(it.collector.name, Normalizer.Form.NFD) }
        }
    }

    @Insert
    protected abstract suspend fun insertCollectors(collectors: List<Collector>)

    @Query("DELETE FROM collector")
    protected abstract suspend fun deleteCollectors()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertCollectorFavoritePerformers(collectorFavoritePerformers: List<CollectorFavoritePerformer>)

    @Query("DELETE FROM collectorfavoriteperformer")
    protected abstract suspend fun deleteAllCollectorFavoritePerformer()

    @Delete
    abstract suspend fun deleteCollectorFavoritePerformer(collectorFavoritePerformer: CollectorFavoritePerformer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertPerformers(performers: List<Performer>)

    // Refresh the list of collectors and their favorite performers
    // To make sure that the database is consistent it is necessary to update the performers
    // associated with the collectors as well
    @Transaction
    open suspend fun deleteAndInsertCollectors(collectors: List<CollectorJson>) {
        val albums: MutableList<Album> = mutableListOf()
        val collectorAlbums: MutableList<CollectorAlbumCrossRef> = mutableListOf()
        val performers: MutableList<Performer> = mutableListOf()
        val collectorFavoritePerformers: MutableList<CollectorFavoritePerformer> = mutableListOf()
        val mappedCollectors = collectors.map { collector ->
            val favoritePerformers: List<Performer> = requireNotNull(collector.favoritePerformers).map { it.toPerformer() }
            performers.addAll(favoritePerformers)
            favoritePerformers.forEach { favPerformer ->
                collectorFavoritePerformers.add(
                    CollectorFavoritePerformer(collector.id, favPerformer.id)
                )
            }

            val collectorAlbumsList = requireNotNull(collector.collectorAlbums).map { it.toCollectorAlbum(collector.id) }
            collectorAlbums.addAll(collectorAlbumsList)

            collector.toCollector()
        }

        deleteCollectors()
        insertCollectors(mappedCollectors)
        deleteAllCollectorFavoritePerformer()
        insertCollectorFavoritePerformers(collectorFavoritePerformers)
        insertPerformers(performers)
    }


}
