package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumTrackViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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

class AlbumTrackViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {
        override fun getAlbums(): Flow<List<Album>> {
            throw UnsupportedOperationException()
        }

        override fun getAlbum(albumId: Int): Flow<Album?> {
            throw UnsupportedOperationException()
        }

        var failAddTrack: Boolean = false
        var addTrackCalled: Boolean = false
        var addTrackAlbumId: Int? = null
        var addTrackName: String? = null
        var addTrackDuration: String? = null

        override suspend fun addTrack(albumId: Int, name: String, duration: String) {
            addTrackCalled = true

            addTrackAlbumId = albumId
            addTrackName = name
            addTrackDuration = duration

            if (failAddTrack)
                throw Exception()
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
            throw UnsupportedOperationException()
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
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun validateNameFailsEmpty() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertFalse(viewModel.validateName(""))
    }

    @Test
    fun validateNameFailsLarge() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertFalse(viewModel.validateName("a".repeat(201)))
    }

    @Test
    fun validateNameLarge() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertTrue(viewModel.validateName("a".repeat(200)))
    }

    @Test
    fun validateNameShort() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertTrue(viewModel.validateName("a"))
    }

    @Test
    fun validateDurationFails() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertFalse(viewModel.validateDuration("01:60"))
        assertFalse(viewModel.validateDuration("01:5"))
    }

    @Test
    fun validateDurationSuccess() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertTrue(viewModel.validateDuration("12:34"))
        assertTrue(viewModel.validateDuration("4:12"))
    }

    @Test
    fun addTrackSuccess() = runTest {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )
        val name = faker.name.name()
        val duration = "${faker.random.nextInt(10, 59)}:${faker.random.nextInt(10, 59)}"

        viewModel.onSave(name, duration)

        assertTrue(albumRepository.addTrackCalled)
        assertEquals(albumId, albumRepository.addTrackAlbumId)
        assertEquals(name, albumRepository.addTrackName)
        assertEquals(duration, albumRepository.addTrackDuration)
    }

    @Test
    fun addTrackFail() = runTest {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumTrackViewModel.Factory.create(
            AlbumTrackViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumTrackViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumTrackViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumTrackViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )
        val name = faker.name.name()
        val duration = "${faker.random.nextInt(10, 59)}:${faker.random.nextInt(10, 59)}"

        albumRepository.failAddTrack = true

        viewModel.onSave(name, duration)

        assertTrue(albumRepository.addTrackCalled)
        assertEquals(albumId, albumRepository.addTrackAlbumId)
        assertEquals(name, albumRepository.addTrackName)
        assertEquals(duration, albumRepository.addTrackDuration)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
