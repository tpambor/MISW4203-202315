package co.edu.uniandes.misw4203.equipo11.vinilos

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumListViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class AlbumListViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {
        private val flow = MutableSharedFlow<List<Album>>()
        suspend fun emit(value: List<Album>) = flow.emit(value)

        var failRefresh = false
        var refreshCalled = false

        override fun getAlbums(): Flow<List<Album>> {
            return flow
        }

        override fun getAlbum(albumId: Int): Flow<Album?> {
            throw UnsupportedOperationException()
        }

        override suspend fun addTrack(albumId: Int, name: String, duration: String) {
            throw UnsupportedOperationException()
        }

        override fun getPerformers(albumId: Int): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getComments(albumId: Int): Flow<List<Comment>> {
            throw UnsupportedOperationException()
        }

        override fun getTracks(albumId: Int): Flow<List<Track>> {
            throw UnsupportedOperationException()
        }

        override suspend fun refresh() {
            refreshCalled = true

            if (failRefresh)
                throw Exception()
        }

        override suspend fun needsRefresh(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshAlbum(albumId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun insertAlbum(album: AlbumRequestJson) {
            throw UnsupportedOperationException()
        }

        override suspend fun addComment(albumId: Int, collectorId: Int, rating: Int, comment: String) {
            throw UnsupportedOperationException()
        }
    }

    @Test
    fun listsAlbums() = runTest {
        val repository = FakeAlbumRepository()
        val viewModel = AlbumListViewModel(repository, Dispatchers.Main)
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
    fun refreshSuccess() = runTest {
        val repository = FakeAlbumRepository()
        val viewModel = AlbumListViewModel(repository, Dispatchers.Main)

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
        val viewModel = AlbumListViewModel(repository, Dispatchers.Main)

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
