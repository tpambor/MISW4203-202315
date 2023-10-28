package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.AlbumListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.viewmodels.ErrorUiState
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

class FakeAlbumRepository: IAlbumRepository {
    private val flow = MutableSharedFlow<List<Album>?>()
    suspend fun emit(value: List<Album>?) = flow.emit(value)

    var refreshed = false

    override fun getAlbums(): Flow<List<Album>?> {
        return flow
    }

    override suspend fun refresh() {
        refreshed = true
    }
}

class AlbumListViewModelTest {
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

        val data = listOf(
            Album("ABC", "DEF", "GHI"),
            Album("XYZ", "123", "...")
        )

        // Initially, there are no albums yet
        assertEquals(emptyList<Album>(), viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first().errorState)

        // Repository emits albums
        repository.emit(data)

        // Then, list of albums is filled with the data
        assertEquals(data, viewModel.albums.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first().errorState)
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
        assertEquals(ErrorUiState.NoError, viewModel.error.first().errorState)

        // Repository emits null (unable to fetch data)
        repository.emit(null)

        // Then, list of albums is filled with the data
        assertEquals(emptyList<Album>(), viewModel.albums.first())

        val error = viewModel.error.value
        assert(error.errorState is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error.errorState as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
    }

    @Test
    fun doesRefresh() {
        val repository = FakeAlbumRepository()

        val viewModel = AlbumListViewModel.Factory.create(
            AlbumListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumListViewModel.KEY_ALBUM_REPOSITORY, repository)
            }
        )

        assertFalse(repository.refreshed)
        viewModel.onRefresh()
        assertTrue(repository.refreshed)
    }
}
