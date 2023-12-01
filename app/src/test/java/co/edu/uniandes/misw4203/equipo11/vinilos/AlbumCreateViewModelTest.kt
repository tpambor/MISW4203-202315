package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumRequestJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumCreateViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.Instant

class AlbumCreateViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {
        override fun getAlbums(): Flow<List<Album>> {
            throw UnsupportedOperationException()
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
            throw UnsupportedOperationException()
        }

        override suspend fun needsRefresh(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshAlbum(albumId: Int) {
            throw UnsupportedOperationException()
        }

        var failInsertAlbum: Boolean = false
        var insertAlbumCalled: Boolean = false
        var insertAlbumAlbum: AlbumRequestJson? = null

        override suspend fun insertAlbum(album: AlbumRequestJson) {
            insertAlbumCalled = true
            insertAlbumAlbum = album

            if (failInsertAlbum)
                throw Exception()
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
        val albumRepository = FakeAlbumRepository()

        val viewModel = AlbumCreateViewModel.Factory.create(
            AlbumCreateViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCreateViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val albumRepository = FakeAlbumRepository()

        val viewModel = AlbumCreateViewModel.Factory.create(
            AlbumCreateViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCreateViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCreateViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun addCommentSuccess() = runTest {
        val albumRepository = FakeAlbumRepository()

        val viewModel = AlbumCreateViewModel.Factory.create(
            AlbumCreateViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCreateViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCreateViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()
        val album = AlbumRequestJson(
            name = faker.music.albums(),
            cover = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            releaseDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())).toString(),
            description = faker.quote.yoda(),
            genre = faker.music.genres(),
            recordLabel = faker.random.randomValue(listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records"))
        )

        viewModel.insertAlbum(album)

        Assert.assertTrue(albumRepository.insertAlbumCalled)
        Assert.assertEquals(album, albumRepository.insertAlbumAlbum)
    }

    @Test
    fun addCommentFail() = runTest {
        val albumRepository = FakeAlbumRepository()

        val viewModel = AlbumCreateViewModel.Factory.create(
            AlbumCreateViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCreateViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCreateViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()
        val album = AlbumRequestJson(
            name = faker.music.albums(),
            cover = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            releaseDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())).toString(),
            description = faker.quote.yoda(),
            genre = faker.music.genres(),
            recordLabel = faker.random.randomValue(listOf("Sony Music", "EMI", "Discos Fuentes", "Elektra", "Fania Records"))
        )

        albumRepository.failInsertAlbum = true

        viewModel.insertAlbum(album)

        Assert.assertTrue(albumRepository.insertAlbumCalled)
        Assert.assertEquals(album, albumRepository.insertAlbumAlbum)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        TestCase.assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        TestCase.assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
