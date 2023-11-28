package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJsonRequest
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IUserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumCommentViewModel
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import io.github.serpro69.kfaker.Faker
import junit.framework.TestCase
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

class AlbumCommentViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {
        override fun getAlbums(): Flow<Result<List<Album>>> {
            throw UnsupportedOperationException()
        }

        override fun getAlbum(albumId: Int): Flow<Album?> {
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

        override suspend fun refreshAlbum(albumId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun insertAlbum(album: AlbumJsonRequest) {
            throw UnsupportedOperationException()
        }

        var addCommentCalled: Boolean = false
        var failAddComment: Boolean = false
        var addCommentAlbumId: Int? = null
        var addCommentCollectorId: Int? = null
        var addCommentRating: Int? = null
        var addCommentComment: String? = null

        override suspend fun addComment(albumId: Int, collectorId: Int, rating: Int, comment: String) {
            addCommentCalled = true

            addCommentAlbumId = albumId
            addCommentCollectorId = collectorId
            addCommentRating = rating
            addCommentComment = comment

            if (failAddComment)
                throw Exception()
        }
    }

    class FakeUserRepository: IUserRepository {
        private val flow = MutableSharedFlow<User?>()

        suspend fun emitUser(value: User) = flow.emit(value)

        override fun getUser(): Flow<User?> {
            return flow
        }

        override suspend fun login(userType: UserType) { }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun addCommentSuccess() = runTest {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)
        val userId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val rating = faker.random.nextInt(1, 5)
        val comment = faker.yoda.quotes()

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.onSave(rating, comment)

        assertTrue(albumRepository.addCommentCalled)
        assertEquals(albumId, albumRepository.addCommentAlbumId)
        assertEquals(userId, albumRepository.addCommentCollectorId)
        assertEquals(rating, albumRepository.addCommentRating)
        assertEquals(comment, albumRepository.addCommentComment)
    }

    @Test
    fun addCommentFail() = runTest {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)
        val userId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val rating = faker.random.nextInt(1, 5)
        val comment = faker.yoda.quotes()

        albumRepository.failAddComment = true

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.onSave(rating, comment)

        assertTrue(albumRepository.addCommentCalled)
        assertEquals(albumId, albumRepository.addCommentAlbumId)
        assertEquals(userId, albumRepository.addCommentCollectorId)
        assertEquals(rating, albumRepository.addCommentRating)
        assertEquals(comment, albumRepository.addCommentComment)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        TestCase.assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        TestCase.assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun validateCommentFailsEmpty() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertFalse(viewModel.validateComment(""))
    }

    @Test
    fun validateCommentFailsTooLarge() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertFalse(viewModel.validateComment("a".repeat(2001)))
    }

    @Test
    fun validateCommentShort() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertTrue(viewModel.validateComment("a"))
    }

    @Test
    fun validateCommentLarge() {
        val faker = Faker()
        val albumRepository = FakeAlbumRepository()
        val userRepository = FakeUserRepository()
        val albumId = faker.random.nextInt(1, 100)

        val viewModel = AlbumCommentViewModel.Factory.create(
            AlbumCommentViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumCommentViewModel.KEY_ALBUM_REPOSITORY, albumRepository)
                set(AlbumCommentViewModel.KEY_USER_REPOSITORY, userRepository)
                set(AlbumCommentViewModel.KEY_ALBUM_ID, albumId)
                set(AlbumCommentViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertTrue(viewModel.validateComment("a".repeat(2000)))
    }
}
