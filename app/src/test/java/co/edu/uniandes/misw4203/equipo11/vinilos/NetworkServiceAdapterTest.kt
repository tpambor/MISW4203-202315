package co.edu.uniandes.misw4203.equipo11.vinilos

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.models.PerformerType
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
import java.time.Instant

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
                releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
                genre = "Salsa",
                recordLabel = "Elektra"
            ),
            Album(
                id = 101,
                name = "Poeta del pueblo",
                cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg",
                releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                description = "Recopilación de 27 composiciones del cosmos Blades que los bailadores y melómanos han hecho suyas en estos 40 años de presencia de los ritmos y concordias afrocaribeños en múltiples escenarios internacionales. Grabaciones de Blades para la Fania con las orquestas de Pete Rodríguez, Ray Barreto, Fania All Stars y, sobre todo, los grandes éxitos con la Banda de Willie Colón",
                genre = "Salsa",
                recordLabel = "Elektra"
            ),
            Album(
                id = 102,
                name = "A Night at the Opera",
                cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                releaseDate = Instant.parse("1975-11-21T00:00:00.000Z"),
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

    @Test
    fun shouldReturnMusicians() = runTest {
        val musiciansJSON = javaClass.getResource("/musicians.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/musicians", url)
            musiciansJSON
        }

        val musiciansExpected = listOf(
            Performer(
                id = 100,
                type = PerformerType.MUSICIAN,
                name = "Rubén Blades Bellido de Luna",
                image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York."
            )
        )

        val adapter = NetworkServiceAdapter()
        val musicians = adapter.getMusicians().first()
        assertEquals(musiciansExpected, musicians)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldReturnBands() = runTest {
        val bandsJSON = javaClass.getResource("/bands.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/bands", url)
            bandsJSON
        }

        val bandsExpected = listOf(
            Performer(
                id = 101,
                type = PerformerType.BAND,
                name = "Queen",
                image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
                birthDate = Instant.parse("1970-01-01T00:00:00.000Z"),
                description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa."
            ),
            Performer(
                id = 2,
                type = PerformerType.BAND,
                name = "The Beatles",
                image = "https://cdn.britannica.com/18/136518-050-CD0E49C6/The-Beatles-Ringo-Starr-Paul-McCartney-George.jpg",
                birthDate = Instant.parse("1960-01-01T00:00:00.000Z"),
                description = "The Beatles, más conocido en el mundo hispano como los Beatles, fue un grupo de rock británico formado en Liverpool durante los años 1960; estando integrado desde 1962 hasta su separación en 1970 por John Lennon, Paul McCartney, George Harrison y Ringo Starr."
            )
        )

        val adapter = NetworkServiceAdapter()
        val bands = adapter.getBands().first()
        assertEquals(bandsExpected, bands)
        assertTrue(mockRequest.called)
    }
}
