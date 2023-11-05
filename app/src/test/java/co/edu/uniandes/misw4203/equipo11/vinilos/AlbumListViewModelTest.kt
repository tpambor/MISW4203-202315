package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumListViewModel
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

class AlbumListViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {
        private val flow = MutableSharedFlow<List<Album>?>()
        suspend fun emit(value: List<Album>?) = flow.emit(value)

        var failRefresh = false
        var refreshCalled = false

        override fun getAlbums(): Flow<List<Album>?> {
            return flow
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
        val repository = FakeAlbumRepository()

        val viewModel = AlbumListViewModel.Factory.create(
            AlbumListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumListViewModel.KEY_ALBUM_REPOSITORY, repository)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun listsAlbums() = runTest {
        val repository = FakeAlbumRepository()

        val viewModel = AlbumListViewModel.Factory.create(
            AlbumListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumListViewModel.KEY_ALBUM_REPOSITORY, repository)
            }
        )

        val faker = Faker()

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
        repository.emit(data)

        // Then, list of albums is filled with the data
        assertEquals(data, viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listAlbumError() = runTest {
        val repository = FakeAlbumRepository()

        val viewModel = AlbumListViewModel.Factory.create(
            AlbumListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumListViewModel.KEY_ALBUM_REPOSITORY, repository)
            }
        )

        // Initially, there are no albums yet
        assertEquals(emptyList<Album>(), viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (unable to fetch data)
        repository.emit(null)

        // Then, list of albums is filled with the data
        assertEquals(emptyList<Album>(), viewModel.albums.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun refreshSuccess() = runTest {
        val repository = FakeAlbumRepository()

        val viewModel = AlbumListViewModel.Factory.create(
            AlbumListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumListViewModel.KEY_ALBUM_REPOSITORY, repository)
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
        val repository = FakeAlbumRepository()

        val viewModel = AlbumListViewModel.Factory.create(
            AlbumListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumListViewModel.KEY_ALBUM_REPOSITORY, repository)
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
