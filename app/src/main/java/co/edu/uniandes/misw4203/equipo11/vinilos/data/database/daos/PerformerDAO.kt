package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorFavoritePerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.MusicianBand
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.toCollector
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

    @Query("SELECT p.* FROM MusicianBand mb JOIN Performer p ON mb.musicianId = p.id WHERE bandId = :performerId")
    fun getBandMembers(performerId: Int): Flow<List<Performer>>

    @Query("SELECT p.* FROM CollectorFavoritePerformer cp JOIN Performer p on cp.performerId = p.id WHERE cp.collectorId = :collectorId")
    fun getFavoritePerformersByCollectorId(collectorId: Int): Flow<List<Performer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPerformers(musicians: List<Performer>)

    @Query("DELETE FROM performer WHERE type = :performerType")
    suspend fun deletePerformersByType(performerType: PerformerType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbums(albums: List<Album>)

    // Internal use only
    @Insert
    suspend fun insertPerformerAlbums(performerAlbum: List<PerformerAlbum>)

    // Internal use only
    @Transaction
    @Query("DELETE FROM PerformerAlbum WHERE performerId IN (SELECT id FROM Performer WHERE Performer.type = :performerType)")
    suspend fun deletePerformerAlbumsByPerformerType(performerType: PerformerType)

    @Query("DELETE FROM PerformerAlbum WHERE performerId = :performerId")
    suspend fun deletePerformerAlbumsByPerformerId(performerId: Int)

    // Internal use only
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectors(collectors: List<Collector>)

    // Internal use only
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectorFavoritePerformers(collectorFavoritePerformers: List<CollectorFavoritePerformer>)

    @Transaction
    @Query("DELETE FROM CollectorFavoritePerformer WHERE performerId IN (SELECT id FROM Performer WHERE Performer.type = :performerType)")
    suspend fun deleteCollectorFavoritePerformerByPerformerType(performerType: PerformerType)

    @Query("DELETE FROM CollectorFavoritePerformer WHERE performerId = :performerId")
    suspend fun deleteCollectorFavoritePerformerByPerformerId(performerId: Int)

    @Query("DELETE FROM MusicianBand")
    suspend fun deleteMusicianBands()

    @Query("DELETE FROM MusicianBand WHERE bandId = :bandId")
    suspend fun deleteMusicianBandsByBandId(bandId: Int)

    @Insert
    suspend fun insertMusicianBands(musicianBands: List<MusicianBand>)

    // Refresh the list of musicians and their collectors
    // To make sure that the database is consistent it is necessary to update the collectors
    // associated with the musicians as well
    @Transaction
    suspend fun deleteAndInsertMusicians(musicians: List<MusicianJson>, deleteAll: Boolean = true) {
        val collectors: MutableList<Collector> = mutableListOf()
        val collectorFavoritePerformers: MutableList<CollectorFavoritePerformer> = mutableListOf()

        val albums: MutableList<Album> = mutableListOf()
        val performerAlbums: MutableList<PerformerAlbum> = mutableListOf()

        val mappedMusicians = musicians.map { musician ->
            val favoriteCollectors: List<Collector> = requireNotNull(musician.collectors).map { it.toCollector() }
            collectors.addAll(favoriteCollectors)
            favoriteCollectors.forEach { favCollector ->
                collectorFavoritePerformers.add(
                    CollectorFavoritePerformer(favCollector.id, musician.id)
                )
            }

            val musicianAlbums = requireNotNull(musician.albums).map { it.toAlbum() }
            albums.addAll(musicianAlbums)
            musicianAlbums.forEach { album ->
                performerAlbums.add(PerformerAlbum(musician.id, album.id))
            }

            musician.toPerformer()
        }

        if (deleteAll)
            deletePerformersByType(PerformerType.MUSICIAN)
        insertPerformers(mappedMusicians)

        insertCollectors(collectors)
        if (deleteAll)
            deleteCollectorFavoritePerformerByPerformerType(PerformerType.MUSICIAN)
        else {
            mappedMusicians.forEach { musician ->
                deleteCollectorFavoritePerformerByPerformerId(musician.id)
            }
        }
        insertCollectorFavoritePerformers(collectorFavoritePerformers)

        insertAlbums(albums)
        if (deleteAll)
            deletePerformerAlbumsByPerformerType(PerformerType.MUSICIAN)
        else {
            mappedMusicians.forEach { musician ->
                deletePerformerAlbumsByPerformerId(musician.id)
            }
        }
        insertPerformerAlbums(performerAlbums)
    }

    // Refresh the list of bands and their collectors
    // To make sure that the database is consistent it is necessary to update the collectors
    // associated with the bands as well
    @Transaction
    suspend fun deleteAndInsertBands(bands: List<BandJson>, deleteAll: Boolean = true) {
        val collectors: MutableList<Collector> = mutableListOf()
        val collectorFavoritePerformers: MutableList<CollectorFavoritePerformer> = mutableListOf()

        val albums: MutableList<Album> = mutableListOf()
        val performerAlbums: MutableList<PerformerAlbum> = mutableListOf()

        val musicians: MutableList<Performer> = mutableListOf()
        val musicianBands: MutableList<MusicianBand> = mutableListOf()

        val mappedBands = bands.map { band ->
            val favoriteCollectors: List<Collector> = requireNotNull(band.collectors).map { it.toCollector() }
            collectors.addAll(favoriteCollectors)
            favoriteCollectors.forEach { favCollector ->
                collectorFavoritePerformers.add(
                    CollectorFavoritePerformer(favCollector.id, band.id)
                )
            }

            val bandAlbums = requireNotNull(band.albums).map { it.toAlbum() }
            albums.addAll(bandAlbums)
            bandAlbums.forEach { album ->
                performerAlbums.add(PerformerAlbum(band.id, album.id))
            }

            val bandMusicians = requireNotNull(band.musicians).map { it.toPerformer() }
            musicians.addAll(bandMusicians)
            bandMusicians.forEach { musician ->
                musicianBands.add(MusicianBand(musician.id, band.id))
            }

            band.toPerformer()
        }

        if (deleteAll)
            deletePerformersByType(PerformerType.BAND)
        insertPerformers(mappedBands)

        insertCollectors(collectors)
        if (deleteAll)
            deleteCollectorFavoritePerformerByPerformerType(PerformerType.BAND)
        else {
            mappedBands.forEach { band ->
                deleteCollectorFavoritePerformerByPerformerId(band.id)
            }
        }
        insertCollectorFavoritePerformers(collectorFavoritePerformers)

        insertAlbums(albums)
        if (deleteAll)
            deletePerformerAlbumsByPerformerType(PerformerType.BAND)
        else {
            mappedBands.forEach { band ->
                deletePerformerAlbumsByPerformerId(band.id)
            }
        }
        insertPerformerAlbums(performerAlbums)

        insertPerformers(musicians)
        if (deleteAll)
            deleteMusicianBands()
        else {
            mappedBands.forEach { band ->
                deleteMusicianBandsByBandId(band.id)
            }
        }
        insertMusicianBands(musicianBands)
    }
}
