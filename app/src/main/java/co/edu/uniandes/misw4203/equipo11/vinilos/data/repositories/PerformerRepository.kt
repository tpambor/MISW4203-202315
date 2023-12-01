package co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.VinilosDB
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Cache
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorFavoritePerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.MusicianBand
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.time.Duration
import java.time.Instant


interface IPerformerRepository {
    fun getMusicians(): Flow<List<Performer>>
    fun getBands(): Flow<List<Performer>>
    fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>>
    fun getMusician(performerId: Int): Flow<Performer?>
    fun getBand(performerId: Int): Flow<Performer?>
    fun getPerformer(performerId: Int): Flow<Performer?>
    fun getBandMembers(performerId: Int): Flow<List<Performer>>
    fun getBandMemberCandidates(): Flow<List<Performer>>
    suspend fun addBandMember(bandId: Int, musicianId: Int)
    fun getAlbums(performerId: Int): Flow<List<Album>>
    fun getAlbumCandidates(performerId: Int): Flow<List<Album>>
    suspend fun addAlbum(performerId: Int, type: PerformerType, albumId: Int)
    suspend fun addFavoriteMusician(collectorId: Int, performerId: Int)
    suspend fun addFavoriteBand(collectorId: Int, performerId: Int)
    suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int)
    suspend fun removeFavoriteBand(collectorId: Int, performerId: Int)
    suspend fun refreshMusicians()
    suspend fun needsRefreshMusicians(): Boolean
    suspend fun refreshMusician(performerId: Int)
    suspend fun refreshBands()
    suspend fun needsRefreshBands(): Boolean
    suspend fun refreshBand(performerId: Int)
}

class PerformerRepository : IPerformerRepository{
    private val adapter = NetworkServiceAdapter()
    private val db = VinilosDB.getInstance()

    override fun getMusicians(): Flow<List<Performer>> = flow {
        db.performerDao().getMusicians().collect { musicians ->
            emit(musicians)
        }
    }

    override fun getBands(): Flow<List<Performer>> = flow {
        db.performerDao().getBands().collect { bands ->
            emit(bands)
        }
    }

    override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> = flow {
        db.performerDao().getFavoritePerformersByCollectorId(collectorId).collect { performers ->
            emit(performers)
        }
    }

    override fun getMusician(performerId: Int): Flow<Performer?> = flow {
        db.performerDao().getMusicianById(performerId).collect { musician ->
            emit(musician)
        }
    }

    override fun getBand(performerId: Int): Flow<Performer?> = flow {
        db.performerDao().getBandById(performerId).collect { band ->
            emit(band)
        }
    }

    override fun getPerformer(performerId: Int): Flow<Performer?> = flow {
        db.performerDao().getPerformerById(performerId).collect { performer ->
            emit(performer)
        }
    }

    override fun getAlbums(performerId: Int): Flow<List<Album>> = flow {
        db.albumDao().getAlbumsByPerformerId(performerId).collect { albums ->
            emit(albums)
        }
    }

    override fun getAlbumCandidates(performerId: Int): Flow<List<Album>> = flow {
        db.albumDao().getAlbumsNotByPerformerId(performerId).collect { albums ->
            emit(albums)
        }
    }

    override fun getBandMembers(performerId: Int): Flow<List<Performer>> = flow {
        db.performerDao().getBandMembers(performerId).collect { musicians ->
            emit(musicians)
        }
    }

    override fun getBandMemberCandidates(): Flow<List<Performer>> = flow {
        db.performerDao().getBandMemberCandidates().collect { musicians ->
            emit(musicians)
        }
    }

    override suspend fun addBandMember(bandId: Int, musicianId: Int) {
        adapter.addBandMember(bandId, musicianId).first()

        db.performerDao().insertMusicianBands(listOf(
            MusicianBand(musicianId, bandId)
        ))
    }

    override suspend fun addAlbum(performerId: Int, type: PerformerType, albumId: Int) {
        when (type) {
            PerformerType.MUSICIAN -> adapter.addMusicianToAlbum(performerId, albumId).first()
            PerformerType.BAND -> adapter.addBandToAlbum(performerId, albumId).first()
        }

        db.performerDao().insertPerformerAlbums(listOf(
            PerformerAlbum(performerId, albumId)
        ))
    }

    override suspend fun addFavoriteMusician(collectorId: Int, performerId: Int) {
        adapter.addFavoriteMusicianToCollector(collectorId, performerId).first()

        db.collectorDao().insertCollectorFavoritePerformers(listOf(
            CollectorFavoritePerformer(collectorId, performerId)
        ))
    }

    override suspend fun addFavoriteBand(collectorId: Int, performerId: Int) {
        adapter.addFavoriteBandToCollector(collectorId, performerId).first()

        db.collectorDao().insertCollectorFavoritePerformers(listOf(
            CollectorFavoritePerformer(collectorId, performerId)
        ))
    }

    override suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int) {
        adapter.removeFavoriteMusicianFromCollector(collectorId, performerId).first()

        db.collectorDao().deleteCollectorFavoritePerformer(
            CollectorFavoritePerformer(collectorId, performerId)
        )
    }

    override suspend fun removeFavoriteBand(collectorId: Int, performerId: Int) {
        adapter.removeFavoriteBandFromCollector(collectorId, performerId).first()

        db.collectorDao().deleteCollectorFavoritePerformer(
            CollectorFavoritePerformer(collectorId, performerId)
        )
    }

    override suspend fun refreshMusicians() {
        db.performerDao().deleteAndInsertMusicians(
            adapter.getMusicians().first()
        )

        db.cacheDao().setLastUpdate(Cache("musicians", Instant.now()))
    }

    override suspend fun needsRefreshMusicians(): Boolean {
        val lastUpdate = db.cacheDao().getLastUpdate("musicians") ?: return true

        return Duration.between(lastUpdate, Instant.now()) > Duration.ofDays(1)
    }

    override suspend fun refreshMusician(performerId: Int) {
        db.performerDao().deleteAndInsertMusicians(
            listOf(adapter.getMusician(performerId).first()),
            deleteAll = false
        )
    }

    override suspend fun refreshBands() {
        db.performerDao().deleteAndInsertBands(
            adapter.getBands().first()
        )

        db.cacheDao().setLastUpdate(Cache("bands", Instant.now()))
    }

    override suspend fun needsRefreshBands(): Boolean {
        val lastUpdate = db.cacheDao().getLastUpdate("bands") ?: return true

        return Duration.between(lastUpdate, Instant.now()) > Duration.ofDays(1)
    }

    override suspend fun refreshBand(performerId: Int) {
        db.performerDao().deleteAndInsertBands(
            listOf(adapter.getBand(performerId).first()),
            deleteAll = false
        )
    }
}
