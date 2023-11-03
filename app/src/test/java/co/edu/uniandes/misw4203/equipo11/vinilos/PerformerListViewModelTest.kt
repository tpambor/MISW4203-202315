package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.PerformerListViewModel
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.Instant

class PerformerListViewModelTest {
    class FakePerformerRepository: IPerformerRepository {
        private val musiciansFlow = MutableSharedFlow<List<Performer>?>()
        private val bandsFlow = MutableSharedFlow<List<Performer>?>()
        var failMusiciansRefresh = false
        var failBandsRefresh = false
        var refreshMusiciansCalled = false
        var refreshBandsCalled = false

        suspend fun emitMusicians(value: List<Performer>?) = musiciansFlow.emit(value)
        suspend fun emitBands(value: List<Performer>?) = bandsFlow.emit(value)
        override fun getMusicians(): Flow<List<Performer>?> = musiciansFlow
        override fun getBands(): Flow<List<Performer>?> = bandsFlow
        override suspend fun refreshMusicians(): Boolean {
            refreshMusiciansCalled = true
            return !failMusiciansRefresh
        }
        override suspend fun refreshBands(): Boolean {
            refreshBandsCalled = true
            return !failBandsRefresh
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertNotNull(viewModel)
    }

    // Tiene en cuenta lo que está pasando dentro del init
    @Test
    fun listsMusicians() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        val faker = Faker()

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

        assertEquals(emptyList<Performer>(), viewModel.musicians.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        repository.emitMusicians(data)

        assertEquals(data, viewModel.musicians.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    // Tiene en cuenta lo que está pasando dentro del init
    @Test
    fun listsBands() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        val faker = Faker()

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

        assertEquals(emptyList<Performer>(), viewModel.bands.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        repository.emitBands(data)

        assertEquals(data, viewModel.bands.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun musiciansError() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertEquals(emptyList<Performer>(), viewModel.musicians.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        repository.emitMusicians(null)

        assertEquals(emptyList<Performer>(), viewModel.musicians.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun bandsError() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertEquals(emptyList<Performer>(), viewModel.bands.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        repository.emitBands(null)

        assertEquals(emptyList<Performer>(), viewModel.bands.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun refreshMusiciansSuccess() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failMusiciansRefresh = false

        assertFalse(repository.refreshMusiciansCalled)
        viewModel.onRefreshMusicians()
        assertTrue(repository.refreshMusiciansCalled)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshMusiciansFail() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failMusiciansRefresh = true

        assertFalse(repository.refreshMusiciansCalled)
        viewModel.onRefreshMusicians()
        assertTrue(repository.refreshMusiciansCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun refreshBandsSuccess() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failBandsRefresh = false

        assertFalse(repository.refreshBandsCalled)
        viewModel.onRefreshBands()
        assertTrue(repository.refreshBandsCalled)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshBandsFail() = runTest {
        val repository = FakePerformerRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        repository.failBandsRefresh = true

        assertFalse(repository.refreshBandsCalled)
        viewModel.onRefreshBands()
        assertTrue(repository.refreshBandsCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }
}