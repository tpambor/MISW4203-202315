package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbumStatus
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.ICollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.CollectorViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.Instant

class CollectorViewModelTest {
    class FakeCollectorRepository(private val expectedCollectorId: Int): ICollectorRepository {
        private val collectorFlow = MutableSharedFlow<Collector?>()
        suspend fun emitCollector(collector: Collector?) = collectorFlow.emit(collector)

        private val albumsFlow = MutableSharedFlow<List<CollectorAlbum>>()
        suspend fun emitAlbums(albums: List<CollectorAlbum>) = albumsFlow.emit(albums)

        private val favoritePerformersFlow = MutableSharedFlow<List<Performer>>()
        suspend fun emitFavoritePerformers(performers: List<Performer>) = favoritePerformersFlow.emit(performers)

        var failRefresh = false
        var refreshCalled = false

        override fun getCollectorsWithFavoritePerformers(): Flow<Result<List<CollectorWithPerformers>>> {
            throw UnsupportedOperationException()
        }

        override fun getCollector(collectorId: Int): Flow<Collector?> {
            assertEquals(expectedCollectorId, collectorId)

            return collectorFlow
        }

        override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> {
            assertEquals(expectedCollectorId, collectorId)

            return favoritePerformersFlow
        }

        override fun getAlbums(collectorId: Int): Flow<List<CollectorAlbum>> {
            assertEquals(expectedCollectorId, collectorId)

            return albumsFlow
        }

        override suspend fun refresh() {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshCollector(collectorId: Int) {
            assertEquals(expectedCollectorId, collectorId)

            refreshCalled = true

            if (failRefresh)
                throw Exception()
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
            },
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
            },
        )

        assertNotNull(viewModel)
    }

   @Test
   fun getsCollector() = runTest {
       val faker = Faker()
       val collectorId = faker.random.nextInt(1, 100)
       val repository = FakeCollectorRepository(collectorId)

       val viewModel = CollectorViewModel.Factory.create(
           CollectorViewModel::class.java,
           MutableCreationExtras(CreationExtras.Empty).apply {
               set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
               set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
           },
       )

       val data = Collector(
           id = collectorId,
           name = faker.name.name(),
           telephone = faker.phoneNumber.phoneNumber(),
           email = faker.internet.email()
       )

       // Initially, there is no collector yet
       assertEquals(null, viewModel.collector.first())
       assertEquals(ErrorUiState.NoError, viewModel.error.first())

       // Repository emits collector
       repository.emitCollector(data)

       // Then, the collector is available
       assertEquals(data, viewModel.collector.first())
       assertEquals(ErrorUiState.NoError, viewModel.error.first())
   }

    @Test
    fun getsCollectorError() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
            },
        )

        // Initially, there is no collector yet
        assertEquals(null, viewModel.collector.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (collector not found)
        repository.emitCollector(null)

        // Then, there is still no collector and a error is generated
        assertEquals(null, viewModel.collector.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsFavoritePerformers() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
            },
        )

        val data = (1..4).map { id ->
            Performer(
                id = id,
                type = PerformerType.MUSICIAN,
                name = faker.name.name(),
                image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
                description = faker.quote.yoda(),
                birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
            )
        }

        // Initially, there are no favorite performers yet
        assertEquals(emptyList<Performer>(), viewModel.favoritePerformers.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits favorite performers
        repository.emitFavoritePerformers(data)

        // Then, the favorite performers are available
        assertEquals(data, viewModel.favoritePerformers.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsAlbums() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
            },
        )

        val data = (1..4).map { id ->
            CollectorAlbum(
                collectorId = collectorId,
                album = Album(
                    id = faker.random.nextInt(1, 100),
                    name = faker.name.name(),
                    cover = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
                    releaseDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
                    description = faker.lorem.words(),
                    genre = faker.music.genres(),
                    recordLabel = faker.company.name()
                ),
                price = faker.random.nextInt(1, 100),
                status = if (faker.random.nextBoolean()) CollectorAlbumStatus.Active else CollectorAlbumStatus.Inactive
            )
        }

        // Initially, there are no albums yet
        assertEquals(emptyList<CollectorAlbum>(), viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits albums
        repository.emitAlbums(data)

        // Then, the albums are available
        assertEquals(data, viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshSuccess() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
            },
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failRefresh = false

        TestCase.assertFalse(repository.refreshCalled)
        viewModel.onRefresh()
        TestCase.assertTrue(repository.refreshCalled)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshFail() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository(collectorId)

        val viewModel = CollectorViewModel.Factory.create(
            CollectorViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorViewModel.KEY_COLLECTOR_REPOSITORY, repository)
                set(CollectorViewModel.KEY_COLLECTOR_ID, collectorId)
                set(CollectorViewModel.KEY_DISPATCHER, Dispatchers.Main)
            },
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failRefresh = true

        TestCase.assertFalse(repository.refreshCalled)
        viewModel.onRefresh()
        TestCase.assertTrue(repository.refreshCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
