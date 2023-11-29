package co.edu.uniandes.misw4203.equipo11.vinilos

import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.HttpRequestQueue
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.NetworkServiceAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJsonRequest
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorAlbumJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CommentJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.PerformerJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.TrackJson
import io.github.serpro69.kfaker.Faker
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

    class MockPostRequest(val handler: (String, String) -> String) {
        var called = false

        init {
            mockkObject(HttpRequestQueue)
            val urlSlot = slot<String>()
            val contentSlot = slot<String>()
            every { HttpRequestQueue.post(capture(urlSlot), capture(contentSlot)) } answers {
                called = true

                flow {
                    emit(handler(urlSlot.captured, contentSlot.captured))
                }
            }
        }
    }

    class MockDeleteRequest(val handler: (String) -> String) {
        var called = false

        init {
            mockkObject(HttpRequestQueue)
            val urlSlot = slot<String>()
            every { HttpRequestQueue.delete(capture(urlSlot)) } answers {
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
            AlbumJson(
                id = 100,
                name = "Buscando América",
                cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
                releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
                genre = "Salsa",
                recordLabel = "Elektra",
                tracks = listOf(
                    TrackJson(
                        id = 100,
                        name = "Decisiones",
                        duration = "5:05"
                    ),
                    TrackJson(
                        id = 101,
                        name = "Desapariciones",
                        duration = "6:29"
                    )
                ),
                performers = listOf(
                    PerformerJson.Musician(MusicianJson(
                        id = 100,
                        name = "Rubén Blades Bellido de Luna",
                        image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                        description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                        birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                        albums = null,
                        collectors = null
                    ))
                ),
                comments = listOf(
                    CommentJson(
                        id = 100,
                        description = "The most relevant album of Ruben Blades",
                        rating = 5
                    )
                )
            ),
            AlbumJson(
                id = 101,
                name = "Poeta del pueblo",
                cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg",
                releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                description = "Recopilación de 27 composiciones del cosmos Blades que los bailadores y melómanos han hecho suyas en estos 40 años de presencia de los ritmos y concordias afrocaribeños en múltiples escenarios internacionales. Grabaciones de Blades para la Fania con las orquestas de Pete Rodríguez, Ray Barreto, Fania All Stars y, sobre todo, los grandes éxitos con la Banda de Willie Colón",
                genre = "Salsa",
                recordLabel = "Elektra",
                tracks = emptyList(),
                performers = listOf(
                    PerformerJson.Musician(MusicianJson(
                        id = 100,
                        name = "Rubén Blades Bellido de Luna",
                        image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                        description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                        birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                        albums = null,
                        collectors = null
                    ))
                ),
                comments = emptyList()
            ),
            AlbumJson(
                id = 102,
                name = "A Night at the Opera",
                cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                releaseDate = Instant.parse("1975-11-21T00:00:00.000Z"),
                description = "Es el cuarto álbum de estudio de la banda británica de rock Queen, publicado originalmente en 1975. Coproducido por Roy Thomas Baker y Queen, A Night at the Opera fue, en el tiempo de su lanzamiento, la producción más cara realizada.1\u200B Un éxito comercial, el álbum fue votado por el público y citado por publicaciones musicales como uno de los mejores trabajos de Queen y de la historia del rock.",
                genre = "Rock",
                recordLabel = "EMI",
                tracks = emptyList(),
                performers = listOf(
                    PerformerJson.Band(BandJson(
                        id = 101,
                        name = "Queen",
                        image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
                        creationDate = Instant.parse("1970-01-01T00:00:00.000Z"),
                        description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.",
                        albums = null,
                        musicians = null,
                        collectors = null
                    ))
                ),
                comments = listOf(
                    CommentJson(
                        id = 101,
                        description = "I love this album of Queen",
                        rating = 5
                    )
                )
            ),
        )

        val adapter = NetworkServiceAdapter()
        val albums = adapter.getAlbums().first()
        assertEquals(albumsExpected, albums)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldReturnAlbum() = runTest {
        val albumId = 100
        val albumJSON = javaClass.getResource("/album.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/albums/$albumId", url)
            albumJSON
        }

        val albumExpected = AlbumJson(
            id = 100,
            name = "Buscando América",
            cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
            releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
            description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
            genre = "Salsa",
            recordLabel = "Elektra",
            tracks = listOf(
                TrackJson(
                    id = 100,
                    name = "Decisiones",
                    duration = "5:05"
                ),
                TrackJson(
                    id = 101,
                    name = "Desapariciones",
                    duration = "6:29"
                )
            ),
            performers = listOf(
                PerformerJson.Musician(MusicianJson(
                    id = 100,
                    name = "Rubén Blades Bellido de Luna",
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                    description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                    birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                    albums = null,
                    collectors = null
                ))
            ),
            comments = listOf(
                CommentJson(
                    id = 100,
                    description = "The most relevant album of Ruben Blades",
                    rating = 5
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val album = adapter.getAlbum(albumId).first()
        assertEquals(albumExpected, album)
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
            MusicianJson(
                id = 100,
                name = "Rubén Blades Bellido de Luna",
                image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                albums = listOf(
                    AlbumJson(
                        id = 100,
                        name = "Buscando América",
                        cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
                        releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                        description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
                        genre = "Salsa",
                        recordLabel = "Elektra",
                        tracks = null,
                        performers = null,
                        comments = null
                    ),
                    AlbumJson(
                        id = 101,
                        name = "Poeta del pueblo",
                        cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg",
                        releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                        description = "Recopilación de 27 composiciones del cosmos Blades que los bailadores y melómanos han hecho suyas en estos 40 años de presencia de los ritmos y concordias afrocaribeños en múltiples escenarios internacionales. Grabaciones de Blades para la Fania con las orquestas de Pete Rodríguez, Ray Barreto, Fania All Stars y, sobre todo, los grandes éxitos con la Banda de Willie Colón",
                        genre = "Salsa",
                        recordLabel = "Elektra",
                        tracks = null,
                        performers = null,
                        comments = null
                    )
                ),
                collectors = listOf(
                    CollectorJson(
                        id = 2,
                        name = "Fernando Cañellas Hervás",
                        telephone = "+34916 03 21 53",
                        email = "onino@gmail.com",
                        favoritePerformers = null,
                        collectorAlbums = null
                    ),
                    CollectorJson(
                        id = 29,
                        name = "Amílcar Sales Maldonado",
                        telephone = "+34827647490",
                        email = "lladoconcepcion@gmail.com",
                        favoritePerformers = null,
                        collectorAlbums = null
                    )
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val musicians = adapter.getMusicians().first()
        assertEquals(musiciansExpected, musicians)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldReturnMusician() = runTest {
        val musicianId = 100
        val musicianJSON = javaClass.getResource("/musician.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/musicians/$musicianId", url)
            musicianJSON
        }

        val musicianExpected = MusicianJson(
            id = 100,
            name = "Rubén Blades Bellido de Luna",
            image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
            birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
            description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
            albums = listOf(
                AlbumJson(
                    id = 100,
                    name = "Buscando América",
                    cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
                    releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                    description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
                    genre = "Salsa",
                    recordLabel = "Elektra",
                    tracks = null,
                    performers = null,
                    comments = null
                ),
                AlbumJson(
                    id = 101,
                    name = "Poeta del pueblo",
                    cover = "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg",
                    releaseDate = Instant.parse("1984-08-01T00:00:00.000Z"),
                    description = "Recopilación de 27 composiciones del cosmos Blades que los bailadores y melómanos han hecho suyas en estos 40 años de presencia de los ritmos y concordias afrocaribeños en múltiples escenarios internacionales. Grabaciones de Blades para la Fania con las orquestas de Pete Rodríguez, Ray Barreto, Fania All Stars y, sobre todo, los grandes éxitos con la Banda de Willie Colón",
                    genre = "Salsa",
                    recordLabel = "Elektra",
                    tracks = null,
                    performers = null,
                    comments = null
                )
            ),
            collectors = listOf(
                CollectorJson(
                    id = 2,
                    name = "Fernando Cañellas Hervás",
                    telephone = "+34916 03 21 53",
                    email = "onino@gmail.com",
                    favoritePerformers = null,
                    collectorAlbums = null
                ),
                CollectorJson(
                    id = 29,
                    name = "Amílcar Sales Maldonado",
                    telephone = "+34827647490",
                    email = "lladoconcepcion@gmail.com",
                    favoritePerformers = null,
                    collectorAlbums = null
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val musician = adapter.getMusician(musicianId).first()
        assertEquals(musicianExpected, musician)
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
            BandJson(
                id = 101,
                name = "Queen",
                image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
                creationDate = Instant.parse("1970-01-01T00:00:00.000Z"),
                description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.",
                albums = listOf(
                    AlbumJson(
                        id = 102,
                        name = "A Night at the Opera",
                        cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                        releaseDate = Instant.parse("1975-11-21T00:00:00.000Z"),
                        description = "Es el cuarto álbum de estudio de la banda británica de rock Queen, publicado originalmente en 1975. Coproducido por Roy Thomas Baker y Queen, A Night at the Opera fue, en el tiempo de su lanzamiento, la producción más cara realizada.1​ Un éxito comercial, el álbum fue votado por el público y citado por publicaciones musicales como uno de los mejores trabajos de Queen y de la historia del rock.",
                        genre = "Rock",
                        recordLabel = "EMI",
                        tracks = null,
                        performers = null,
                        comments = null
                    ),
                    AlbumJson(
                        id = 103,
                        name = "A Day at the Races",
                        cover = "https://www.udiscovermusic.com/wp-content/uploads/2019/11/a-day-at-the-races.jpg",
                        releaseDate = Instant.parse("1976-12-10T00:00:00.000Z"),
                        description = "El álbum fue grabado en los Estudios Sarm West, The Manor and Wessex en Inglaterra y con el ingeniero Mike Stone. El título del álbum es una referencia directa al anterior, A Night at the Opera. Ambos álbumes están titulados como películas de los hermanos Marx.",
                        genre = "Rock",
                        recordLabel = "EMI",
                        tracks = null,
                        performers = null,
                        comments = null
                    )
                ),
                musicians = listOf(
                    MusicianJson(
                        id = 100,
                        name = "Rubén Blades Bellido de Luna",
                        image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                        birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                        description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                        albums = null,
                        collectors = null
                    )
                ),
                collectors = listOf(
                    CollectorJson(
                        id = 2,
                        name = "Fernando Cañellas Hervás",
                        telephone = "+34916 03 21 53",
                        email = "onino@gmail.com",
                        favoritePerformers = null,
                        collectorAlbums = null
                    ),
                    CollectorJson(
                        id = 29,
                        name = "Amílcar Sales Maldonado",
                        telephone = "+34827647490",
                        email = "lladoconcepcion@gmail.com",
                        favoritePerformers = null,
                        collectorAlbums = null
                    )
                )
            ),
            BandJson(
                id = 2,
                name = "The Beatles",
                image = "https://cdn.britannica.com/18/136518-050-CD0E49C6/The-Beatles-Ringo-Starr-Paul-McCartney-George.jpg",
                creationDate = Instant.parse("1960-01-01T00:00:00.000Z"),
                description = "The Beatles, más conocido en el mundo hispano como los Beatles, fue un grupo de rock británico formado en Liverpool durante los años 1960; estando integrado desde 1962 hasta su separación en 1970 por John Lennon, Paul McCartney, George Harrison y Ringo Starr.",
                albums = emptyList(),
                musicians = emptyList(),
                collectors = emptyList()
            )
        )

        val adapter = NetworkServiceAdapter()
        val bands = adapter.getBands().first()
        assertEquals(bandsExpected, bands)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldReturnBand() = runTest {
        val bandId = 101
        val bandJSON = javaClass.getResource("/band.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/bands/$bandId", url)
            bandJSON
        }

        val bandExpected = BandJson(
            id = 101,
            name = "Queen",
            image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
            creationDate = Instant.parse("1970-01-01T00:00:00.000Z"),
            description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.",
            albums = listOf(
                AlbumJson(
                    id = 102,
                    name = "A Night at the Opera",
                    cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                    releaseDate = Instant.parse("1975-11-21T00:00:00.000Z"),
                    description = "Es el cuarto álbum de estudio de la banda británica de rock Queen, publicado originalmente en 1975. Coproducido por Roy Thomas Baker y Queen, A Night at the Opera fue, en el tiempo de su lanzamiento, la producción más cara realizada.1​ Un éxito comercial, el álbum fue votado por el público y citado por publicaciones musicales como uno de los mejores trabajos de Queen y de la historia del rock.",
                    genre = "Rock",
                    recordLabel = "EMI",
                    tracks = null,
                    performers = null,
                    comments = null
                ),
                AlbumJson(
                    id = 103,
                    name = "A Day at the Races",
                    cover = "https://www.udiscovermusic.com/wp-content/uploads/2019/11/a-day-at-the-races.jpg",
                    releaseDate = Instant.parse("1976-12-10T00:00:00.000Z"),
                    description = "El álbum fue grabado en los Estudios Sarm West, The Manor and Wessex en Inglaterra y con el ingeniero Mike Stone. El título del álbum es una referencia directa al anterior, A Night at the Opera. Ambos álbumes están titulados como películas de los hermanos Marx.",
                    genre = "Rock",
                    recordLabel = "EMI",
                    tracks = null,
                    performers = null,
                    comments = null
                )
            ),
            musicians = listOf(
                MusicianJson(
                    id = 100,
                    name = "Rubén Blades Bellido de Luna",
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                    birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                    description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                    albums = null,
                    collectors = null
                )
            ),
            collectors = listOf(
                CollectorJson(
                    id = 2,
                    name = "Fernando Cañellas Hervás",
                    telephone = "+34916 03 21 53",
                    email = "onino@gmail.com",
                    favoritePerformers = null,
                    collectorAlbums = null
                ),
                CollectorJson(
                    id = 29,
                    name = "Amílcar Sales Maldonado",
                    telephone = "+34827647490",
                    email = "lladoconcepcion@gmail.com",
                    favoritePerformers = null,
                    collectorAlbums = null
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val band = adapter.getBand(101).first()
        assertEquals(bandExpected, band)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldReturnCollectors() = runTest {
        val collectorsJSON = javaClass.getResource("/collectors.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/collectors", url)
            collectorsJSON
        }

        val collectorsExpected = listOf(
            CollectorJson(
                id = 100,
                name = "Manolo Bellon",
                telephone = "3502457896",
                email = "manollo@caracol.com.co",
                favoritePerformers = listOf(
                    PerformerJson.Musician(MusicianJson(
                        id = 100,
                        name = "Rubén Blades Bellido de Luna",
                        image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                        description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                        birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                        albums = null,
                        collectors = null
                    )),
                    PerformerJson.Band(BandJson(
                        id = 101,
                        name = "Queen",
                        image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
                        description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.",
                        creationDate = Instant.parse("1970-01-01T00:00:00.000Z"),
                        albums = null,
                        musicians = null,
                        collectors = null
                    ))
                ),
                collectorAlbums = listOf(
                    CollectorAlbumJson(
                        id = 1,
                        price = 35,
                        status = "Active",
                        AlbumJson(
                            id = 102,
                            name = "A Night at the Opera",
                            cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                            releaseDate = Instant.parse("1975-11-21T00:00:00.000Z"),
                            description = "Es el cuarto álbum de estudio de la banda británica de rock Queen, publicado originalmente en 1975. Coproducido por Roy Thomas Baker y Queen, A Night at the Opera fue, en el tiempo de su lanzamiento, la producción más cara realizada.1​ Un éxito comercial, el álbum fue votado por el público y citado por publicaciones musicales como uno de los mejores trabajos de Queen y de la historia del rock.",
                            genre = "Rock",
                            recordLabel = "EMI",
                            tracks = null,
                            performers = null,
                            comments = null
                        )
                    ),
                    CollectorAlbumJson(
                        id = 2,
                        price = 25000,
                        status = "Inactive",
                        album = AlbumJson(
                            id = 1,
                            name = "Radio Rompecorazones",
                            cover = "https://i.scdn.co/image/ab67616d0000b273816a542c3c2e281501275aa5",
                            releaseDate = Instant.parse("2008-09-12T00:00:00.000Z"),
                            description = "Mind among sure perhaps. Exactly choose foreign north.",
                            genre = "Salsa",
                            recordLabel = "Discos Fuentes",
                            tracks = null,
                            performers = null,
                            comments = null
                        )
                    )
                )
            ),
            CollectorJson(
                id = 101,
                name = "Jaime Monsalve",
                telephone = "3012357936",
                email = "jmonsalve@rtvc.com.co",
                favoritePerformers = listOf(
                    PerformerJson.Band(BandJson(
                        id = 101,
                        name = "Queen",
                        image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
                        description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.",
                        creationDate = Instant.parse("1970-01-01T00:00:00.000Z"),
                        albums = null,
                        musicians = null,
                        collectors = null
                    ))
                ),
                collectorAlbums = listOf(
                    CollectorAlbumJson(
                        id = 3,
                        price = 25,
                        status = "Active",
                        album = AlbumJson(
                            id = 1,
                            name = "Radio Rompecorazones",
                            cover = "https://i.scdn.co/image/ab67616d0000b273816a542c3c2e281501275aa5",
                            releaseDate = Instant.parse("2008-09-12T00:00:00.000Z"),
                            description = "Mind among sure perhaps. Exactly choose foreign north.",
                            genre = "Salsa",
                            recordLabel = "Discos Fuentes",
                            tracks = null,
                            performers = null,
                            comments = null
                        )
                    )
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val collectors = adapter.getCollectors().first()
        assertEquals(collectorsExpected, collectors)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldReturnCollector() = runTest {
        val collectorId = 100
        val collectorJSON = javaClass.getResource("/collector.json").readText()
        val mockRequest = MockRequest { url ->
            assertEquals( NetworkServiceAdapter.API_BASE_URL + "/collectors/$collectorId", url)
            collectorJSON
        }

        val collectorExpected = CollectorJson(
            id = 100,
            name = "Manolo Bellon",
            telephone = "3502457896",
            email = "manollo@caracol.com.co",
            favoritePerformers = listOf(
                PerformerJson.Musician(MusicianJson(
                    id = 100,
                    name = "Rubén Blades Bellido de Luna",
                    image = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/bb/Ruben_Blades_by_Gage_Skidmore.jpg/800px-Ruben_Blades_by_Gage_Skidmore.jpg",
                    description = "Es un cantante, compositor, músico, actor, abogado, político y activista panameño. Ha desarrollado gran parte de su carrera artística en la ciudad de Nueva York.",
                    birthDate = Instant.parse("1948-07-16T00:00:00.000Z"),
                    albums = null,
                    collectors = null
                )),
                PerformerJson.Band(BandJson(
                    id = 101,
                    name = "Queen",
                    image = "https://pm1.narvii.com/6724/a8b29909071e9d08517b40c748b6689649372852v2_hq.jpg",
                    description = "Queen es una banda británica de rock formada en 1970 en Londres por el cantante Freddie Mercury, el guitarrista Brian May, el baterista Roger Taylor y el bajista John Deacon. Si bien el grupo ha presentado bajas de dos de sus miembros (Mercury, fallecido en 1991, y Deacon, retirado en 1997), los integrantes restantes, May y Taylor, continúan trabajando bajo el nombre Queen, por lo que la banda aún se considera activa.",
                    creationDate = Instant.parse("1970-01-01T00:00:00.000Z"),
                    albums = null,
                    musicians = null,
                    collectors = null
                ))
            ),
            collectorAlbums = listOf(
                CollectorAlbumJson(
                    id = 1,
                    price = 35,
                    status = "Active",
                    AlbumJson(
                        id = 102,
                        name = "A Night at the Opera",
                        cover = "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png",
                        releaseDate = Instant.parse("1975-11-21T00:00:00.000Z"),
                        description = "Es el cuarto álbum de estudio de la banda británica de rock Queen, publicado originalmente en 1975. Coproducido por Roy Thomas Baker y Queen, A Night at the Opera fue, en el tiempo de su lanzamiento, la producción más cara realizada.1​ Un éxito comercial, el álbum fue votado por el público y citado por publicaciones musicales como uno de los mejores trabajos de Queen y de la historia del rock.",
                        genre = "Rock",
                        recordLabel = "EMI",
                        tracks = null,
                        performers = null,
                        comments = null
                    )
                ),
                CollectorAlbumJson(
                    id = 2,
                    price = 25000,
                    status = "Inactive",
                    album = AlbumJson(
                        id = 1,
                        name = "Radio Rompecorazones",
                        cover = "https://i.scdn.co/image/ab67616d0000b273816a542c3c2e281501275aa5",
                        releaseDate = Instant.parse("2008-09-12T00:00:00.000Z"),
                        description = "Mind among sure perhaps. Exactly choose foreign north.",
                        genre = "Salsa",
                        recordLabel = "Discos Fuentes",
                        tracks = null,
                        performers = null,
                        comments = null
                    )
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val collector = adapter.getCollector(100).first()
        assertEquals(collectorExpected, collector)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldAddFavoriteMusician() = runTest {
        val collectorId = 1
        val musicianId = 1044
        val addMusicianJSON = javaClass.getResource("/addFavoriteMusician.json").readText()
        val mockRequest = MockPostRequest { url, content ->
            assertEquals( "${NetworkServiceAdapter.API_BASE_URL}/collectors/$collectorId/musicians/$musicianId", url)
            assertEquals("", content)
            addMusicianJSON
        }

        val expectedPerformer = MusicianJson(
            id = 1044,
            name = "Aventura",
            image = "https://i.scdn.co/image/ab6761610000e5eb4cd0464ef7d8eb8521f72dd8",
            description = "Maybe table give now partner. Recently campaign send pressure yes large themselves.",
            birthDate = Instant.parse("1912-04-24T00:00:00.000Z"),
            albums = null,
            collectors = listOf(
                CollectorJson(
                    id = 18,
                    name = "Celia Núñez Peral",
                    telephone = "+34 822 976 548",
                    email = "garridoaurelio@chico-perez.es",
                    favoritePerformers = null,
                    collectorAlbums = null
                ),
                CollectorJson(
                    id = 57,
                    name = "Cleto Bertrán Morán",
                    telephone = "+34841 738 680",
                    email = "kperello@yahoo.com",
                    favoritePerformers = null,
                    collectorAlbums = null
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val performer = adapter.addFavoriteMusicianToCollector(collectorId, musicianId).first()
        assertEquals(expectedPerformer, performer)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldAddFavoriteBand() = runTest {
        val collectorId = 2
        val bandId = 12
        val addMusicianJSON = javaClass.getResource("/addFavoriteBand.json").readText()
        val mockRequest = MockPostRequest { url, content ->
            assertEquals( "${NetworkServiceAdapter.API_BASE_URL}/collectors/$collectorId/bands/$bandId", url)
            assertEquals("", content)
            addMusicianJSON
        }

        val expectedPerformer = BandJson(
            id = 12,
            name = "Grupo Niche",
            image = "https://i.scdn.co/image/ab6761610000e5eb1ede9ddcf3ca7ebc0de49652",
            description = "Reach role another agree future term officer. Drug standard million evidence expert. Ask drop conference attorney themselves.",
            creationDate = Instant.parse("1922-09-12T00:00:00.000Z"),
            albums = null,
            musicians = null,
            collectors = listOf(
                CollectorJson(
                    id = 29,
                    name = "Amílcar Sales Maldonado",
                    telephone = "+34827647490",
                    email = "lladoconcepcion@gmail.com",
                    favoritePerformers = null,
                    collectorAlbums = null
                ),
                CollectorJson(
                    id = 34,
                    name = "Fabiana Alcalde Bayón",
                    telephone = "+34976997506",
                    email = "montserratnunez@galan.es",
                    favoritePerformers = null,
                    collectorAlbums = null
                )
            )
        )

        val adapter = NetworkServiceAdapter()
        val performer = adapter.addFavoriteBandToCollector(collectorId, bandId).first()
        assertEquals(expectedPerformer, performer)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldRemoveFavoriteMusician() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(0, 100)
        val musicianId = faker.random.nextInt(0, 100)

        val mockRequest = MockDeleteRequest { url ->
            assertEquals( "${NetworkServiceAdapter.API_BASE_URL}/collectors/$collectorId/musicians/$musicianId", url)
            ""
        }

        val adapter = NetworkServiceAdapter()
        adapter.removeFavoriteMusicianFromCollector(collectorId, musicianId).first()
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldRemoveFavoriteBand() = runTest {
        val faker = Faker()
        val collectorId = faker.random.nextInt(0, 100)
        val bandId = faker.random.nextInt(0, 100)

        val mockRequest = MockDeleteRequest { url ->
            assertEquals( "${NetworkServiceAdapter.API_BASE_URL}/collectors/$collectorId/bands/$bandId", url)
            ""
        }

        val adapter = NetworkServiceAdapter()
        adapter.removeFavoriteBandFromCollector(collectorId, bandId).first()
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldAddCommentToAlbum() = runTest {
        val albumId = 1
        val collectorId = 2
        val rating = 4
        val description = "It is an amazing album"
        val addCommentJSON = javaClass.getResource("/addComment.json").readText()
        val mockRequest = MockPostRequest { url, content ->
            assertEquals( "${NetworkServiceAdapter.API_BASE_URL}/albums/$albumId/comments", url)
            assertEquals("{\"description\":\"$description\",\"rating\":$rating,\"collector\":{\"id\":$collectorId}}", content)
            addCommentJSON
        }

        val expectedComment = CommentJson(
            id = 1503,
            rating = rating,
            description = description
        )

        val adapter = NetworkServiceAdapter()
        val comment = adapter.addCommentToAlbum(albumId, collectorId, rating, description).first()
        assertEquals(expectedComment, comment)
        assertTrue(mockRequest.called)
    }

    @Test
    fun shouldInsertAlbum() = runTest {
        val addAlbumJSON = javaClass.getResource("/addAlbum.json").readText()
        val mockRequest = MockPostRequest { url, content ->
            assertEquals( "${NetworkServiceAdapter.API_BASE_URL}/albums", url)
            assertEquals(addAlbumJSON, content)
            addAlbumJSON
        }

        val albumJsonRequest = AlbumJsonRequest(
            name = "Buscando América",
            cover = "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg",
            releaseDate = "1984-08-01T00:00:00-05:00",
            description = "Buscando América es el primer álbum de la banda de Rubén Blades y Seis del Solar lanzado en 1984. La producción, bajo el sello Elektra, fusiona diferentes ritmos musicales tales como la salsa, reggae, rock, y el jazz latino. El disco fue grabado en Eurosound Studios en Nueva York entre mayo y agosto de 1983.",
            genre = "Salsa",
            recordLabel ="Elektra",
        )

        val adapter = NetworkServiceAdapter()
        adapter.insertAlbum(albumJsonRequest).first()
        assertTrue(mockRequest.called)
    }
}
