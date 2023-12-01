package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.User
import co.edu.uniandes.misw4203.equipo11.vinilos.data.datastore.models.UserType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IUserRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.ErrorUiState
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.PerformerListViewModel
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
        private val musiciansFlow = MutableSharedFlow<List<Performer>>()
        private val bandsFlow = MutableSharedFlow<List<Performer>>()
        private val favoritesFlow = MutableSharedFlow<List<Performer>>()

        var failMusiciansRefresh = false
        var failBandsRefresh = false
        var refreshMusiciansCalled = false
        var refreshBandsCalled = false

        suspend fun emitMusicians(value: List<Performer>) = musiciansFlow.emit(value)
        suspend fun emitBands(value: List<Performer>) = bandsFlow.emit(value)
        suspend fun emitFavoritePerformers(value: List<Performer>) = favoritesFlow.emit(value)

        override fun getMusicians(): Flow<List<Performer>> = musiciansFlow
        override fun getBands(): Flow<List<Performer>> = bandsFlow
        override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> = favoritesFlow
        
        override fun getMusician(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBand(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getPerformer(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBandMembers(performerId: Int): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getBandMemberCandidates(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override suspend fun addBandMember(bandId: Int, musicianId: Int) {
            throw UnsupportedOperationException()
        }

        override fun getAlbums(performerId: Int): Flow<List<Album>> {
            throw UnsupportedOperationException()
        }

        override fun getAlbumCandidates(performerId: Int): Flow<List<Album>> {
            throw UnsupportedOperationException()
        }

        override suspend fun addAlbum(performerId: Int, type: PerformerType, albumId: Int) {
            throw UnsupportedOperationException()
        }

        var failUpdateFavorite: Boolean = false
        var updateFavoriteMusicianCollectorId: Int? = null
        var updateFavoriteMusicianPerformerId: Int? = null
        var updateFavoriteAction: String? = null

        override suspend fun addFavoriteMusician(collectorId: Int, performerId: Int) {
            updateFavoriteAction = "AddMusician"
            updateFavoriteMusicianCollectorId = collectorId
            updateFavoriteMusicianPerformerId = performerId

            if (failUpdateFavorite)
                throw Exception()
        }

        override suspend fun addFavoriteBand(collectorId: Int, performerId: Int) {
            updateFavoriteAction = "AddBand"
            updateFavoriteMusicianCollectorId = collectorId
            updateFavoriteMusicianPerformerId = performerId

            if (failUpdateFavorite)
                throw Exception()
        }

        override suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int) {
            updateFavoriteAction = "RemoveMusician"
            updateFavoriteMusicianCollectorId = collectorId
            updateFavoriteMusicianPerformerId = performerId

            if (failUpdateFavorite)
                throw Exception()
        }

        override suspend fun removeFavoriteBand(collectorId: Int, performerId: Int) {
            updateFavoriteAction = "RemoveBand"
            updateFavoriteMusicianCollectorId = collectorId
            updateFavoriteMusicianPerformerId = performerId

            if (failUpdateFavorite)
                throw Exception()
        }

        override suspend fun refreshMusicians() {
            refreshMusiciansCalled = true

            if (failMusiciansRefresh)
                throw Exception()
        }

        override suspend fun needsRefreshMusicians(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshMusician(performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshBands() {
            refreshBandsCalled = true

            if (failBandsRefresh)
                throw Exception()
        }

        override suspend fun needsRefreshBands(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshBand(performerId: Int) {
            throw UnsupportedOperationException()
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
            }
        )

        assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertNotNull(viewModel)
    }

    // Tiene en cuenta lo que está pasando dentro del init
    @Test
    fun listsMusicians() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
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
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
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
    fun listsFavoritePerformers() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
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

        assertEquals(emptySet<Int>(), viewModel.favoritePerformers.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        userRepository.emitUser(User(UserType.Collector, 1))
        repository.emitFavoritePerformers(data)

        assertEquals(data.map { it.id }.toSet(), viewModel.favoritePerformers.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshMusiciansSuccess() = runTest {
        val performerRepository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, performerRepository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        performerRepository.failMusiciansRefresh = false

        assertFalse(performerRepository.refreshMusiciansCalled)
        viewModel.onRefreshMusicians()
        assertTrue(performerRepository.refreshMusiciansCalled)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshMusiciansFail() = runTest {
        val performerRepository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, performerRepository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        performerRepository.failMusiciansRefresh = true

        assertFalse(performerRepository.refreshMusiciansCalled)
        viewModel.onRefreshMusicians()
        assertTrue(performerRepository.refreshMusiciansCalled)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshBandsSuccess() = runTest {
        val performerRepository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, performerRepository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        assertEquals(ErrorUiState.NoError, viewModel.error.first())
        performerRepository.failBandsRefresh = false

        assertFalse(performerRepository.refreshBandsCalled)
        viewModel.onRefreshBands()
        assertTrue(performerRepository.refreshBandsCalled)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun refreshBandsFail() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
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
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addFavoriteMusicianSuccess() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.addFavoriteMusician(performerId)

        assertEquals("AddMusician", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addFavoriteMusicianFail() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        repository.failUpdateFavorite = true

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.addFavoriteMusician(performerId)

        assertEquals("AddMusician", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addFavoriteBandSuccess() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.addFavoriteBand(performerId)

        assertEquals("AddBand", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addFavoriteBandFail() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        repository.failUpdateFavorite = true

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.addFavoriteBand(performerId)

        assertEquals("AddBand", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun removeFavoriteMusicianSuccess() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.removeFavoriteMusician(performerId)

        assertEquals("RemoveMusician", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun removeFavoriteMusicianFail() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        repository.failUpdateFavorite = true

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.removeFavoriteMusician(performerId)

        assertEquals("RemoveMusician", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun removeFavoriteBandSuccess() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.removeFavoriteBand(performerId)

        assertEquals("RemoveBand", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun removeFavoriteBandFail() = runTest {
        val repository = FakePerformerRepository()
        val userRepository = FakeUserRepository()

        val viewModel = PerformerListViewModel.Factory.create(
            PerformerListViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(PerformerListViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(PerformerListViewModel.KEY_USER_REPOSITORY, userRepository)
                set(PerformerListViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val faker = Faker()

        val userId = faker.random.nextInt(0, 100)
        val performerId = faker.random.nextInt(1, 100)

        repository.failUpdateFavorite = true

        userRepository.emitUser(User(UserType.Collector, userId))
        viewModel.removeFavoriteBand(performerId)

        assertEquals("RemoveBand", repository.updateFavoriteAction)
        assertEquals(userId, repository.updateFavoriteMusicianCollectorId)
        assertEquals(performerId, repository.updateFavoriteMusicianPerformerId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
