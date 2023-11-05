package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.ICollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.CollectorListViewModel
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class CollectorListViewModelTest {
    class FakeCollectorRepository : ICollectorRepository {
        private val flowCollectors = MutableSharedFlow<List<Collector>?>()
        private val flowCollectorsWithPerformers = MutableSharedFlow<List<CollectorWithPerformers>?>()
        suspend fun emit(value: List<CollectorWithPerformers>?) = flowCollectorsWithPerformers.emit(value)

        var failRefresh = false
        var refreshCalled = false

        override fun getCollectors(): Flow<List<Collector>?> {
            return flowCollectors
        }

        override fun getCollectorsWithFavoritePerformers(): Flow<List<CollectorWithPerformers>?> {
            return flowCollectorsWithPerformers
        }

        override suspend fun refresh(): Boolean {
            refreshCalled = true

            return !failRefresh
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val repository = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repository)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun listsCollectors() = runTest {
        val repository = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repository)
            }
        )

        val faker = Faker()

        val data = (1..4).map { id ->
            CollectorWithPerformers(
                collector = Collector(
                    id = id,
                    name = faker.name.name(),
                    telephone = faker.phoneNumber.phoneNumber(),
                    email = faker.internet.email()
                ),
                performers = (1..4).map { performerId ->
                    Performer(
                        id = performerId,
                        type = PerformerType.MUSICIAN,
                        name = faker.name.name(),
                        image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
                        description = faker.quote.yoda(),
                        birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
                    )
                }
            )
        }

        // Initially, there are no Collectors yet
        assertEquals(emptyList<CollectorWithPerformers>(), viewModel.collectors.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits Collectors
        repository.emit(data)

        // Then, list of Collectors is filled with the data
        assertEquals(data, viewModel.collectors.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listCollectorError() = runTest {
        val repository = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repository)
            }
        )

        // Initially, there are no Collectors yet
        assertEquals(emptyList<CollectorWithPerformers>(), viewModel.collectors.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (unable to fetch data)
        repository.emit(null)

        // Then, list of Collectors is filled with the data
        assertEquals(emptyList<CollectorWithPerformers>(), viewModel.collectors.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun refreshSuccess() = runTest {
        val repository = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repository)
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
        val repositoryCollector = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repositoryCollector)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repositoryCollector.failRefresh = true

        assertFalse(repositoryCollector.refreshCalled)
        viewModel.onRefresh()
        assertTrue(repositoryCollector.refreshCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }
}
