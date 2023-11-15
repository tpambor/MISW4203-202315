package co.edu.uniandes.misw4203.equipo11.vinilos

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.repositories.IAlbumRepository
import co.edu.uniandes.misw4203.equipo11.vinilos.ui.viewmodels.AlbumViewModel
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.Instant

class AlbumViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {

        private val albumFlow = MutableSharedFlow<List<Album>?>()
        private val performersFlow = MutableSharedFlow<List<Performer>>()
        private val commentsFlow = MutableSharedFlow<List<Comment>>()
        private val tracksFlow = MutableSharedFlow<List<Track>>()

        val albumId = 1

        var getAlbumCalled = false
        var getPerformersCalled = false
        var getCommentsCalled = false
        var getTracksCalled = false

        suspend fun emitAlbum(albums: List<Album>?) = albumFlow.emit(albums)
        suspend fun emitPerformers(performers: List<Performer>) = performersFlow.emit(performers)
        suspend fun emitComments(comments: List<Comment>) = commentsFlow.emit(comments)
        suspend fun emitTracks(tracks: List<Track>) = tracksFlow.emit(tracks)

        override fun getAlbums(): Flow<List<Album>?> = albumFlow.asSharedFlow()

        override suspend fun refresh(): Boolean {
            return true
        }

        override fun getAlbum(albumId: Int): Flow<Album?> {
            getAlbumCalled = true

            val fakeAlbum = Album(
                id = albumId,
                name = "Album $albumId",
                cover = "red",
                releaseDate = Instant.now(),
                description = "Description $albumId",
                genre = "Salsa",
                recordLabel = "Record Label $albumId"
            )

            return flow { emit(fakeAlbum) }
        }

        override fun getPerformanceAlbums(albumId: Int): Flow<List<Performer>> {
            getPerformersCalled = true

            val fakePerformers = listOf(
                Performer(
                    id = 1,
                    name = "Performer 1",
                    image = "red",
                    description = "description 1",
                    birthDate = Instant.now(),
                    type = PerformerType.BAND
                ),
                Performer(
                    id = 2,
                    name = "Performer 2",
                    image = "red",
                    description = "description 2",
                    birthDate = Instant.now(),
                    type = PerformerType.MUSICIAN
                )
            )
            return flow { emit(fakePerformers) }
        }

        override fun getCommentsAlbums(albumId: Int): Flow<List<Comment>> {
            getCommentsCalled = true
            val fakeComments = listOf(
                Comment(
                    id = 1,
                    description = "Description 1",
                    rating = 5,
                ),
                Comment(
                    id = 2,
                    description = "Description 2",
                    rating = 4,
                )
            )
            return flow { emit(fakeComments) }
        }

        override fun getTracksAlbums(albumId: Int): Flow<List<Track>> {
            getTracksCalled = true
            val fakeTracks = listOf(
                Track(
                    id = 1,
                    name = "Track 1",
                    duration = "2:30"

                ),
                Track(
                    id = 2,
                    name = "Track 2",
                    duration = "3:30"

                )
            )
            return flow { emit(fakeTracks) }
        }
    }
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun canCreate() {
        val repository = FakeAlbumRepository()
        val albumId = 1

        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        assertNotNull(viewModel)
    }
}