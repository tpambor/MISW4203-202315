package co.edu.uniandes.misw4203.equipo11.vinilos

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.network.HttpRequestQueue
import co.edu.uniandes.misw4203.equipo11.vinilos.network.NetworkServiceAdapter
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.Date

class NetworkServiceAdapterTest {
    class MockRequest(val handler: (String) -> String) {
        var called = false

        init {
            mockkObject(HttpRequestQueue)
            val urlSlot = slot<String>()
            every { HttpRequestQueue.get(capture(urlSlot)) } answers {
                called = true

                flow {
                    emit(handler(urlSlot.captured))
                }
            }
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun afterTests() {
        unmockkAll()
    }

    @Test
    fun shouldReturnAlbums() = runTest {
        val albumsJSON = javaClass.getResource("/albums.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/albums", url)
            albumsJSON
        }

        val albumsExpected = listOf(
            Album(
                id = 100,
                name = "Buscando América",
                cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
                releaseDate = Date(460166400000),
                description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
                genre = "Salsa",
                recordLabel = "Elektra"
            ),
            Album(
                id = 101,
                name = "Poeta del pueblo",
                cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg",
                releaseDate = Date(460166400000),
                description = "Recopilación de 27 composiciones del cosmos Blades que los bailadores y melómanos han hecho suyas en estos 40 años de presencia de los ritmos y concordias afrocaribeños en múltiples escenarios internacionales. Grabaciones de Blades para la Fania con las orquestas de Pete Rodríguez, Ray Barreto, Fania All Stars y, sobre todo, los grandes éxitos con la Banda de Willie Colón",
                genre = "Salsa",
                recordLabel = "Elektra"
            ),
            Album(
                id = 102,
                name = "A Night at the Opera",
                cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                releaseDate = Date(185760000000),
                description = "Es el cuarto álbum de estudio de la banda británica de rock Queen, publicado originalmente en 1975. Coproducido por Roy Thomas Baker y Queen, A Night at the Opera fue, en el tiempo de su lanzamiento, la producción más cara realizada.1\u200B Un éxito comercial, el álbum fue votado por el público y citado por publicaciones musicales como uno de los mejores trabajos de Queen y de la historia del rock.",
                genre = "Rock",
                recordLabel = "EMI"
            ),
        )

        val adapter = NetworkServiceAdapter()
        val albums = adapter.getAlbums().first()
        assertEquals(albumsExpected, albums)
        assertTrue(mockRequest.called)
    }
}
