package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toAlbum
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

    fun getBandById(performerId: Int): Flow<Performer?> = getPerformersByTypeId(PerformerType.BAND, performerId)

    fun getBands(): Flow<List<Performer>> = getPerformersByType(PerformerType.BAND)

    @Query("SELECT p.* FROM CollectorFavoritePerformer cp JOIN Performer p on cp.performerId = p.id WHERE cp.collectorId = :collectorId")
    fun getFavoritePerformersByCollectorId(collectorId: Int): Flow<List<Performer>>

    @Insert
    suspend fun insertPerformers(musicians: List<Performer>)

    @Query("DELETE FROM performer WHERE type = :performerType")
    suspend fun deletePerformersByType(performerType: PerformerType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<Album>)

    // Internal use only
    @Insert
    suspend fun insertPerformerAlbums(performerAlbum: List<PerformerAlbum>)

    // Internal use only
    @Query("DELETE FROM PerformerAlbum")
    suspend fun deletePerformerAlbums()

    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<MusicianJson>) {
        val performerAlbums: MutableList<PerformerAlbum> = mutableListOf()
        val albums: MutableList<Album> = mutableListOf()
        val performers = musicians.map { musician ->
            if (musician.albums != null) {
                val musicianAlbums = musician.albums.map { album ->
                    performerAlbums.add(PerformerAlbum(musician.id, album.id))

                    album.toAlbum()
                }
                albums.addAll(musicianAlbums)
            }

            musician.toPerformer()
        }
        deletePerformersByType(PerformerType.MUSICIAN)
        insertPerformers(performers)
        insertAlbums(albums)
        deletePerformerAlbums()
        insertPerformerAlbums(performerAlbums)
    }

    @Transaction
    suspend fun deleteAndInsertBands(musicians: List<BandJson>) {
        val performers = musicians.map { it.toPerformer() }
        deletePerformersByType(PerformerType.BAND)
        insertPerformers(performers)
    }
}
