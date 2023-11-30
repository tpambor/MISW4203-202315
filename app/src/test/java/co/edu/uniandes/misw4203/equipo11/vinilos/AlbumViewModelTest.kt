package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
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

class AlbumViewModelTest {
    class FakeAlbumRepository(private val expectedAlbumId: Int): IAlbumRepository {
        private val albumFlow = MutableSharedFlow<Album?>()
        suspend fun emitAlbum(value: Album?) = albumFlow.emit(value)

        private val performersFlow = MutableSharedFlow<List<Performer>>()
        suspend fun emitPerformers(value: List<Performer>) = performersFlow.emit(value)

        private val commentsFlow = MutableSharedFlow<List<Comment>>()
        suspend fun emitComments(value: List<Comment>) = commentsFlow.emit(value)

        private val tracksFlow = MutableSharedFlow<List<Track>>()
        suspend fun emitTracks(value: List<Track>) = tracksFlow.emit(value)

        var failRefresh = false
        var refreshCalled = false

        override fun getAlbums(): Flow<Result<List<Album>>> {
            throw UnsupportedOperationException()
        }

        override fun getAlbum(albumId: Int): Flow<Album?> {
            assertEquals(expectedAlbumId, albumId)

            return albumFlow
        }

        override suspend fun addTrack(albumId: Int, name: String, duration: String) {
            throw UnsupportedOperationException()
        }

        override fun getPerformers(albumId: Int): Flow<List<Performer>> {
            assertEquals(expectedAlbumId, albumId)

            return performersFlow
        }

        override fun getComments(albumId: Int): Flow<List<Comment>> {
            assertEquals(expectedAlbumId, albumId)

            return commentsFlow
        }

        override fun getTracks(albumId: Int): Flow<List<Track>> {
            assertEquals(expectedAlbumId, albumId)

            return tracksFlow
        }

        override suspend fun refresh() {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshAlbum(albumId: Int) {
            assertEquals(expectedAlbumId, albumId)

            refreshCalled = true

            if (failRefresh)
                throw Exception()
        }

        override suspend fun insertAlbum(album: AlbumRequestJson) {
            throw UnsupportedOperationException()
        }

        override suspend fun addComment(albumId: Int, collectorId: Int, rating: Int, comment: String) {
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
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun getsAlbum() = runTest {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = Album(
            id = albumId,
            name = faker.music.albums(),
            cover = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            releaseDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
            description = faker.quote.yoda(),
            genre = faker.music.genres(),
            recordLabel = faker.random.randomValue(listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records"))
        )

        // Initially, there is no album yet
        assertEquals(null, viewModel.album.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits album
        repository.emitAlbum(data)

        // Then, the album is available
        assertEquals(data, viewModel.album.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsMusicianError() = runTest {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        // Initially, there is no album yet
        assertEquals(null, viewModel.album.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (album not found)
        repository.emitAlbum(null)

        // Then, there is still no album and a error is generated
        assertEquals(null, viewModel.album.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsPerformers() = runTest {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
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

        // Initially, there are no performers yet
        assertEquals(emptyList<Performer>(), viewModel.performers.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits performers
        repository.emitPerformers(data)

        // Then, list of performers is filled with the data
        assertEquals(data, viewModel.performers.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsComments() = runTest {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = (1..4).map { id ->
            Comment(
                id = id,
                description = faker.starWars.quote(),
                rating = faker.random.nextInt(1, 5),
                albumId = albumId
            )
        }

        // Initially, there are no comments yet
        assertEquals(emptyList<Comment>(), viewModel.comments.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits comments
        repository.emitComments(data)

        // Then, list of comments is filled with the data
        assertEquals(data, viewModel.comments.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsTracks() = runTest {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = (1..4).map { id ->
            Track(
                id = id,
                name = faker.animal.name(),
                duration = "${faker.random.nextInt(0, 9)}:${faker.random.nextInt(10, 59)}",
                albumId = albumId
            )
        }

        // Initially, there are no tracks yet
        assertEquals(emptyList<Track>(), viewModel.tracks.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits tracks
        repository.emitTracks(data)

        // Then, list of tracks is filled with the data
        assertEquals(data, viewModel.tracks.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshSuccess() = runTest {
        val faker = Faker()
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
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
        val albumId = faker.random.nextInt(1, 100)
        val repository = FakeAlbumRepository(albumId)

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumViewModel.KEY_DISPATCHER, Dispatchers.Main)
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
