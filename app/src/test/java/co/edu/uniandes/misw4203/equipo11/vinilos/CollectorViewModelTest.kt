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
    class FakeCollectorRepository: ICollectorRepository {
        private val collectorFlow = MutableSharedFlow<Collector?>()
        suspend fun emitCollector(collector: Collector?) {
            collectorFlow.emit(collector)
        }

        private val albumsFlow = MutableSharedFlow<List<CollectorAlbum>>()
        suspend fun emitAlbums(albums: List<CollectorAlbum>) {
            albumsFlow.emit(albums)
        }

        private val favoritePerformersFlow = MutableSharedFlow<List<Performer>>()
        suspend fun emitFavoritePerformers(performers: List<Performer>) {
            favoritePerformersFlow.emit(performers)
        }

        var refreshCalled = false
        var failRefresh = false

        override fun getCollectorsWithFavoritePerformers(): Flow<Result<List<CollectorWithPerformers>>> {
            TODO("Not yet implemented")
        }

        override fun getCollector(collectorId: Int): Flow<Collector?> {
            return collectorFlow
        }

        override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> {
            return favoritePerformersFlow
        }

        override fun getAlbums(collectorId: Int): Flow<List<CollectorAlbum>> {
            return albumsFlow
        }

        override suspend fun refresh() {
            TODO("Not yet implemented")
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
        val repository = FakeCollectorRepository()
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
        val repository = FakeCollectorRepository()
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
       val repository = FakeCollectorRepository()

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
           telephone = "1234567890",
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
    fun getsFavoritePerformers() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository()

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
    fun getAlbums() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(1, 100)
        val repository = FakeCollectorRepository()

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
                price  = faker.random.nextInt(1, 100),
                status = CollectorAlbumStatus.Inactive
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
}