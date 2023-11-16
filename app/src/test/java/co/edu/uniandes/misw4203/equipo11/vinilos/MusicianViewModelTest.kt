package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.MusicianViewModel
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

class MusicianViewModelTest {
    class FakePerformerRepository(private val expectedPerformerId: Int): IPerformerRepository {
        private val albumsFlow = MutableSharedFlow<List<Album>>()
        suspend fun emitAlbums(value: List<Album>) = albumsFlow.emit(value)

        private val musicianFlow = MutableSharedFlow<Performer?>()
        suspend fun emitMusician(value: Performer?) = musicianFlow.emit(value)

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
            assertEquals(expectedPerformerId, performerId)

            return musicianFlow
        }

        override fun getBand(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBandMembers(performerId: Int): Flow<List<Performer>> {
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
            assertEquals(expectedPerformerId, performerId)

            refreshCalled = true

            if (failRefresh)
                throw Exception()
        }

        override suspend fun refreshBands() {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshBand(performerId: Int) {
            throw UnsupportedOperationException()
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

        val viewModel = MusicianViewModel.Factory.create(
            MusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
            }
        )

        TestCase.assertNotNull(viewModel)
    }

    @Test
    fun listsAlbums() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = MusicianViewModel.Factory.create(
            MusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
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
    fun getsMusician() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = MusicianViewModel.Factory.create(
            MusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
            }
        )

        val data = Performer(
            id = performerId,
            type = PerformerType.MUSICIAN,
            name = faker.name.name(),
            image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            description = faker.quote.yoda(),
            birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
        )

        // Initially, there is no musician yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits musician
        repository.emitMusician(data)

        // Then, the musician is available
        assertEquals(data, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsMusicianError() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = MusicianViewModel.Factory.create(
            MusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
            }
        )

        // Initially, there is no musician yet
        assertEquals(null, viewModel.performer.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (musician not found)
        repository.emitMusician(null)

        // Then, there is still no musician and a error is generated
        assertEquals(null, viewModel.performer.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun refreshSuccess() = runTest {
        val faker = Faker()
        val performerId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(performerId)

        val viewModel = MusicianViewModel.Factory.create(
            MusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
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

        val viewModel = MusicianViewModel.Factory.create(
            MusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerViewModel.KEY_PERFORMER_ID, performerId)
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
    }
}
