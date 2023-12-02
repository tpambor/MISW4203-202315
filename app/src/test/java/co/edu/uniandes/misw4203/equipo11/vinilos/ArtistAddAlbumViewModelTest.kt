package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ArtistAddAlbumViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ArtistAddAlbumViewModelTest {
    class FakePerformerRepository(private val expectedPerformerId: Int): IPerformerRepository {
        private val albumsFlow = MutableSharedFlow<List<Album>>()
        suspend fun emitAlbums(value: List<Album>) = albumsFlow.emit(value)

        private val performerFlow = MutableSharedFlow<Performer?>()
        suspend fun emitPerformer(value: Performer?) = performerFlow.emit(value)

        override fun getMusicians(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getBands(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getMusician(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBand(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getPerformer(performerId: Int): Flow<Performer?> {
            assertEquals(expectedPerformerId, performerId)

            return performerFlow
        }

        override fun getBandMembers(performerId: Int): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getBandMemberCandidates(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override suspend fun addBandMember(bandId: Int, musicianId: Int) {
            throw UnsupportedOperationException()
        }

        override fun getAlbums(performerId: Int): Flow<List<Album>> {
            throw UnsupportedOperationException()
        }

        override fun getAlbumCandidates(performerId: Int): Flow<List<Album>> {
            assertEquals(expectedPerformerId, performerId)

            return albumsFlow
        }

        var failAddAlbum: Boolean = false
        var addAlbumCalled: Boolean = false
        var addAlbumPerformerId: Int? = null
        var addAlbumPerformerType: PerformerType? = null
        var addAlbumAlbumId: Int? = null

        override suspend fun addAlbum(performerId: Int, type: PerformerType, albumId: Int) {
            addAlbumCalled = true
            addAlbumPerformerId = performerId
            addAlbumPerformerType = type
            addAlbumAlbumId = albumId

            if (failAddAlbum)
                throw Exception()
        }

        override suspend fun addFavoriteMusician(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun addFavoriteBand(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun removeFavoriteBand(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshMusicians() {
            throw UnsupportedOperationException()
        }

        override suspend fun needsRefreshMusicians(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshMusician(performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshBands() {
            throw UnsupportedOperationException()
        }

        override suspend fun needsRefreshBands(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshBand(performerId: Int) {
            throw UnsupportedOperationException()
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreateForBand() {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateForMusician() {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateForBandWithDispatcher() {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateForMusicianWithDispatcher() {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun listsAlbumsForBand() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = (1..4).map { id ->
            Album(
                id = id,
                name = faker.music.albums(),
                cover = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
                releaseDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
                description = faker.quote.yoda(),
                genre = faker.music.genres(),
                recordLabel = faker.random.randomValue(listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records"))
            )
        }

        // Initially, there are no albums yet
        assertEquals(emptyList<Album>(), viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits albums
        repository.emitAlbums(data)

        // Then, list of albums is filled with the data
        assertEquals(data, viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsAlbumsForMusician() = runTest {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = (1..4).map { id ->
            Album(
                id = id,
                name = faker.music.albums(),
                cover = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
                releaseDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
                description = faker.quote.yoda(),
                genre = faker.music.genres(),
                recordLabel = faker.random.randomValue(listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records"))
            )
        }

        // Initially, there are no albums yet
        assertEquals(emptyList<Album>(), viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits albums
        repository.emitAlbums(data)

        // Then, list of albums is filled with the data
        assertEquals(data, viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsPerformerForBand() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = Performer(
            id = bandId,
            type = PerformerType.BAND,
            name = faker.name.name(),
            image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            description = faker.quote.yoda(),
            birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits band
        repository.emitPerformer(data)

        // Then, the band is available
        assertEquals(data, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsPerformerForBandError() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (band not found)
        repository.emitPerformer(null)

        // Then, there is still no band and a error is generated
        assertEquals(null, viewModel.performer.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsPerformerForMusician() = runTest {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = Performer(
            id = musicianId,
            type = PerformerType.MUSICIAN,
            name = faker.name.name(),
            image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            description = faker.quote.yoda(),
            birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits band
        repository.emitPerformer(data)

        // Then, the band is available
        assertEquals(data, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsPerformerForMusicianError() = runTest {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (band not found)
        repository.emitPerformer(null)

        // Then, there is still no band and a error is generated
        assertEquals(null, viewModel.performer.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addAlbumForBandSuccess() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val albumId = faker.random.nextInt(1, 100)

        viewModel.onSave(albumId)

        assertTrue(repository.addAlbumCalled)
        assertEquals(bandId, repository.addAlbumPerformerId)
        assertEquals(PerformerType.BAND, repository.addAlbumPerformerType)
        assertEquals(albumId, repository.addAlbumAlbumId)
    }

    @Test
    fun addAlbumForBandFail() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, bandId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.BAND)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val albumId = faker.random.nextInt(1, 100)

        repository.failAddAlbum = true
        viewModel.onSave(albumId)

        assertTrue(repository.addAlbumCalled)
        assertEquals(bandId, repository.addAlbumPerformerId)
        assertEquals(PerformerType.BAND, repository.addAlbumPerformerType)
        assertEquals(albumId, repository.addAlbumAlbumId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addAlbumForMusicianSuccess() = runTest {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val albumId = faker.random.nextInt(1, 100)

        viewModel.onSave(albumId)

        assertTrue(repository.addAlbumCalled)
        assertEquals(musicianId, repository.addAlbumPerformerId)
        assertEquals(PerformerType.MUSICIAN, repository.addAlbumPerformerType)
        assertEquals(albumId, repository.addAlbumAlbumId)
    }

    @Test
    fun addAlbumForMusicianFail() = runTest {
        val faker = Faker()
        val musicianId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(musicianId)

        val viewModel = ArtistAddAlbumViewModel.Factory.create(
            ArtistAddAlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_ID, musicianId)
                set(ArtistAddAlbumViewModel.KEY_PERFORMER_TYPE, PerformerType.MUSICIAN)
                set(ArtistAddAlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val albumId = faker.random.nextInt(1, 100)

        repository.failAddAlbum = true
        viewModel.onSave(albumId)

        assertTrue(repository.addAlbumCalled)
        assertEquals(musicianId, repository.addAlbumPerformerId)
        assertEquals(PerformerType.MUSICIAN, repository.addAlbumPerformerType)
        assertEquals(albumId, repository.addAlbumAlbumId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
