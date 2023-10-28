package co.edu.uniandes.misw4203.equipo11.vinilos.repositories

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

interface IAlbumRepository {
    fun getAlbums(): Flow<List<Album>?>
    suspend fun refresh()
}

// TODO: Fake repository only! Implement repository!
class AlbumRepository : IAlbumRepository {
    override fun getAlbums(): Flow<List<Album>?> = flow {
        while (true) {
            delay(2000)
            if (_refresh) {
                _refresh = false
                if (Random.nextBoolean())
                    emit(null)
                else
                    emit(albumsMockData)
            }
        }
    }

    override suspend fun refresh() {
        _refresh = true
    }

    private val albumsMockData: List<Album> = listOf(
        Album("Buscando América", "Salsa", "https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61cc8f41d1e79db917a7cd.jpg"),
        Album("Poeta del pueblo", "Salsa", "https://cdn.shopify.com/s/files/1/0275/3095/products/image_4931268b-7acf-4702-9c55-b2b3a03ed999_1024x1024.jpg"),
        Album("A Night at the Opera", "Rock", "https://upload.wikimedia.org/wikipedia/en/4/4d/Queen_A_Night_At_The_Opera.png")
    )

    private var _refresh = true
}
