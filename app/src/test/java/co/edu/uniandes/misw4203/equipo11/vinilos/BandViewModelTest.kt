package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.BandViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.PerformerViewModel
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class BandViewModelTest {
    class FakePerformerRepository(private val expectedPerformerId: Int): IPerformerRepository {
        private val albumsFlow = MutableSharedFlow<List<Album>>()
        suspend fun emitAlbums(value: List<Album>) = albumsFlow.emit(value)

        private val bandFlow = MutableSharedFlow<Performer?>()
        suspend fun emitBand(value: Performer?) = bandFlow.emit(value)

        private val bandMembersFlow = MutableSharedFlow<List<Performer>>()
        suspend fun emitBandMembers(value: List<Performer>) = bandMembersFlow.emit(value)

        var failRefresh = false
        var refreshCalled = false

        override fun getMusicians(): Flow<Result<List<Performer>>> {
            throw UnsupportedOperationException()
        }

        override fun getBands(): Flow<Result<List<Performer>>> {
            throw UnsupportedOperationException()
        }

        override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>>{
            throw UnsupportedOperationException()
        }

        override fun getMusician(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBand(performerId: Int): Flow<Performer?> {
            assertEquals(expectedPerformerId, performerId)

            return bandFlow
        }

        override fun getBandMembers(performerId: Int): Flow<List<Performer>> {
            assertEquals(expectedPerformerId, performerId)

            return bandMembersFlow
        }

        override fun getBandMemberCandidates(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override suspend fun addBandMember(bandId: Int, musicianId: Int) {
            throw UnsupportedOperationException()
        }

        override fun getAlbums(performerId: Int): Flow<List<Album>> {
            assertEquals(expectedPerformerId, performerId)

            return albumsFlow
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

        override suspend fun refreshMusician(performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshBands() {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshBand(performerId: Int) {
            assertEquals(expectedPerformerId, performerId)

            refreshCalled = true

            if (failRefresh)
                throw Exception()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
            }
        )

        TestCase.assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        TestCase.assertNotNull(viewModel)
    }

    @Test
    fun listsAlbums() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
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
    fun listsBandMembers() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = (1..4).map { id ->
            Performer(
                id = id,
                type = PerformerType.BAND,
                name = faker.name.name(),
                image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
                description = faker.quote.yoda(),
                birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
            )
        }

        // Initially, there are no band members yet
        assertEquals(emptyList<Performer>(), viewModel.members.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits band members
        repository.emitBandMembers(data)

        // Then, list of band members is filled with the data
        assertEquals(data, viewModel.members.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsBand() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = Performer(
            id = performerId,
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
        repository.emitBand(data)

        // Then, the band is available
        assertEquals(data, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsBandError() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (band not found)
        repository.emitBand(null)

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
    fun refreshSuccess() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failRefresh = false

        assertFalse(repository.refreshCalled)
        viewModel.onRefresh()
        assertTrue(repository.refreshCalled)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshFail() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = BandViewModel.Factory.create(
            BandViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
                set(PerformerViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failRefresh = true

        assertFalse(repository.refreshCalled)
        viewModel.onRefresh()
        assertTrue(repository.refreshCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
