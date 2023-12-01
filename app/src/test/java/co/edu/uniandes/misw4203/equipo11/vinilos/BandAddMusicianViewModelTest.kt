package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IPerformerRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.BandAddMusicianViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class BandAddMusicianViewModelTest {
    class FakePerformerRepository(private val expectedPerformerId: Int): IPerformerRepository {
        private val bandFlow = MutableSharedFlow<Performer?>()
        suspend fun emitBand(value: Performer?) = bandFlow.emit(value)

        private val bandMemberCandidatesFlow = MutableSharedFlow<List<Performer>>()
        suspend fun emitBandMemberCandidates(value: List<Performer>) = bandMemberCandidatesFlow.emit(value)

        override fun getMusicians(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getBands(): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getFavoritePerformers(collectorId: Int): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getMusician(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBand(performerId: Int): Flow<Performer?> {
            assertEquals(expectedPerformerId, performerId)

            return bandFlow
        }

        override fun getPerformer(performerId: Int): Flow<Performer?> {
            throw UnsupportedOperationException()
        }

        override fun getBandMembers(performerId: Int): Flow<List<Performer>> {
            throw UnsupportedOperationException()
        }

        override fun getBandMemberCandidates(): Flow<List<Performer>> = bandMemberCandidatesFlow

        var failAddBandMember: Boolean = false
        var addBandMemberCalled: Boolean = false
        var addBandMemberBandId: Int? = null
        var addBandMemberMusicianId: Int? = null

        override suspend fun addBandMember(bandId: Int, musicianId: Int) {
            addBandMemberCalled = true
            addBandMemberBandId = bandId
            addBandMemberMusicianId = musicianId

            if (failAddBandMember)
                throw Exception()
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

        override suspend fun addFavoriteMusician(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun addFavoriteBand(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun removeFavoriteMusician(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun removeFavoriteBand(collectorId: Int, performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshMusicians() {
            throw UnsupportedOperationException()
        }

        override suspend fun needsRefreshMusicians(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshMusician(performerId: Int) {
            throw UnsupportedOperationException()
        }

        override suspend fun refreshBands() {
            throw UnsupportedOperationException()
        }

        override suspend fun needsRefreshBands(): Boolean {
            return true // No cache for unit tests
        }

        override suspend fun refreshBand(performerId: Int) {
            throw UnsupportedOperationException()
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
            }
        )

        TestCase.assertNotNull(viewModel)
    }

    @Test
    fun canCreateWithDispatcher() {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
                set(BandAddMusicianViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        TestCase.assertNotNull(viewModel)
    }

    @Test
    fun getsBand() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
                set(BandAddMusicianViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val data = Performer(
            id = bandId,
            type = PerformerType.BAND,
            name = faker.name.name(),
            image = "https://loremflickr.com/480/480/album?lock=${faker.random.nextInt(0, 100)}",
            description = faker.quote.yoda(),
            birthDate = Instant.ofEpochMilli(faker.random.nextLong(System.currentTimeMillis())),
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.band.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits band
        repository.emitBand(data)

        // Then, the band is available
        assertEquals(data, viewModel.band.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun getsBandError() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
                set(BandAddMusicianViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        // Initially, there is no band yet
        assertEquals(null, viewModel.band.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits null (band not found)
        repository.emitBand(null)

        // Then, there is still no band and a error is generated
        assertEquals(null, viewModel.band.first())

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun listsBandMemberCandidates() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
                set(BandAddMusicianViewModel.KEY_DISPATCHER, Dispatchers.Main)
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

        // Initially, there are no band member candidates yet
        assertEquals(emptyList<Performer>(), viewModel.membersCandidates.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())

        // Repository emits band member candidates
        repository.emitBandMemberCandidates(data)

        // Then, list of band member candidates is filled with the data
        assertEquals(data, viewModel.membersCandidates.first())
        assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }

    @Test
    fun addBandMemberSuccess() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
                set(BandAddMusicianViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val musicianId = faker.random.nextInt(1, 100)

        viewModel.onSave(musicianId)

        assertTrue(repository.addBandMemberCalled)
        assertEquals(bandId, repository.addBandMemberBandId)
        assertEquals(musicianId, repository.addBandMemberMusicianId)
    }

    @Test
    fun addBandMemberFail() = runTest {
        val faker = Faker()
        val bandId = faker.random.nextInt(1, 100)
        val repository = FakePerformerRepository(bandId)

        val viewModel = BandAddMusicianViewModel.Factory.create(
            BandAddMusicianViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(BandAddMusicianViewModel.KEY_PERFORMER_REPOSITORY, repository)
                set(BandAddMusicianViewModel.KEY_PERFORMER_ID, bandId)
                set(BandAddMusicianViewModel.KEY_DISPATCHER, Dispatchers.Main)
            }
        )

        val musicianId = faker.random.nextInt(1, 100)

        repository.failAddBandMember = true
        viewModel.onSave(musicianId)

        assertTrue(repository.addBandMemberCalled)
        assertEquals(bandId, repository.addBandMemberBandId)
        assertEquals(musicianId, repository.addBandMemberMusicianId)

        val error = viewModel.error.value
        assert(error is ErrorUiState.Error)
        val errorState: ErrorUiState.Error = error as ErrorUiState.Error
        TestCase.assertEquals(R.string.network_error, errorState.resourceId)
        viewModel.onErrorShown()
        TestCase.assertEquals(ErrorUiState.NoError, viewModel.error.first())
    }
}
