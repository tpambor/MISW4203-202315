package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.ICollectorRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.CollectorListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import io.mockk.internalSubstitute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Date

class CollectorListViewModelTest {
    class FakeCollectorRepository : ICollectorRepository {
        private val flow = MutableSharedFlow<List<Collector>?>()
        private val flowCollectorPerformance = MutableSharedFlow<List<CollectorWithPerformers>?>()
        suspend fun emit(value: List<Collector>?) = flow.emit(value)

        var failRefresh = false
        var refreshCalled = false

        override fun getCollectors(): Flow<List<Collector>?> {
            return flow
        }

        override fun getCollectorsWithFavoritePerformers(): Flow<List<CollectorWithPerformers>?> {
            return flowCollectorPerformance
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

        Assert.assertNotNull(viewModel)
    }

    @Test
    fun listsCollectors() = runTest {
        val repositoryCollector = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repositoryCollector)
            }
        )

        val faker = Faker()

        val data = (1..1).map { id ->
            Collector(
                id = id,
                name = faker.name.name(),
                email =  faker.internet.email(),
                telephone = faker.phoneNumber.phoneNumber()
            )
        }

        // Initially, there are no Collectors yet
        Assert.assertEquals(emptyList<Collector>(), viewModel.collectors.first())
        Assert.assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits Collectors
        repositoryCollector.emit(data)

        // Then, list of Collectors is filled with the data
        Assert.assertEquals(data, viewModel.collectors.first())
        //Assert.assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listCollectorError() = runTest {
        val repositoryCollector = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repositoryCollector)
            }
        )

        // Initially, there are no Collectors yet
        Assert.assertEquals(emptyList<Collector>(), viewModel.collectors.first())
        Assert.assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (unable to fetch data)
        repositoryCollector.emit(null)

        // Then, list of Collectors is filled with the data
        Assert.assertEquals(emptyList<Collector>(), viewModel.collectors.first())

       // val error = viewModel.error.value
        //assert(error is ErrorUiState.Error)
        //val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        //Assert.assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun refreshSuccess() = runTest {
        val repositoryCollector = FakeCollectorRepository()

        val viewModel = CollectorListViewModel.Factory.create(
            CollectorListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(CollectorListViewModel.KEY_COLLECTOR_REPOSITORY, repositoryCollector)
            }
        )

        Assert.assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repositoryCollector.failRefresh = false

        Assert.assertFalse(repositoryCollector.refreshCalled)
        viewModel.onRefresh()
        Assert.assertTrue(repositoryCollector.refreshCalled)
        Assert.assertEquals(ErrorUiState.NoError, viewModel.error.first())
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

        Assert.assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repositoryCollector.failRefresh = true

        Assert.assertFalse(repositoryCollector.refreshCalled)
        viewModel.onRefresh()
        Assert.assertTrue(repositoryCollector.refreshCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        Assert.assertEquals(R.string.network_error, errorState.resourceId)
    }
}