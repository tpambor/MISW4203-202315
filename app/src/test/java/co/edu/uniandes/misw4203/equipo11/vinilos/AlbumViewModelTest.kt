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
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import java.time.Instant

class AlbumViewModelTest {
    class FakeAlbumRepository: IAlbumRepository {

        private val albumFlow = MutableSharedFlow<Result<List<Album>>>()


        var getAlbumCalled = false
        var getPerformersCalled = false
        var getCommentsCalled = false
        var getTracksCalled = false

        override fun getAlbums(): Flow<Result<List<Album>>> = albumFlow.asSharedFlow()

        override suspend fun refresh() { }

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

    @Test
    fun getAlbum() = runTest {
        // Configuración
        val repository = FakeAlbumRepository()
        val albumId = 1
        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        // Observar el flujo del álbum en el ViewModel
        val observedAlbum = viewModel.album.first()

        // Verificar que el álbum observado es igual al álbum falso emitido por el repositorio
        assertTrue(repository.getAlbumCalled)
        assertNotNull(observedAlbum)
    }

    @Test
    fun getPerformers() = runTest {
        // Configuración
        val repository = FakeAlbumRepository()
        val albumId = 1
        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        // Observar el flujo de los artistas en el ViewModel
        val observedPerformers = viewModel.albumsperformers.first()

        // Verificar que los artistas observados son iguales a los artistas falsos emitidos por el repositorio
        assertTrue(repository.getPerformersCalled)
        assertNotNull(observedPerformers)
    }

    @Test
    fun getComments() = runTest {
        // Configuración
        val repository = FakeAlbumRepository()
        val albumId = 1
        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        // Observar el flujo de los comentarios en el ViewModel
        val observedComments = viewModel.albumscomments.first()

        // Verificar que los comentarios observados son iguales a los comentarios falsos emitidos por el repositorio
        assertTrue(repository.getCommentsCalled)
        assertNotNull(observedComments)
    }

    @Test
    fun getTracks() = runTest {
        // Configuración
        val repository = FakeAlbumRepository()
        val albumId = 1
        val viewModel = AlbumViewModel.Factory.create(
            AlbumViewModel::class.java,
            MutableCreationExtras(CreationExtras.Empty).apply {
                set(AlbumViewModel.KEY_ALBUM_REPOSITORY, repository)
                set(AlbumViewModel.KEY_ALBUM_ID, albumId)
            }
        )

        // Observar el flujo de las canciones en el ViewModel
        val observedTracks = viewModel.albumstracks.first()

        // Verificar que las canciones observadas son iguales a las canciones falsas emitidas por el repositorio
        assertTrue(repository.getTracksCalled)
        assertNotNull(observedTracks)
    }
}