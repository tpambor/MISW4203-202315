package co.edu.uniandes.misw4203.equipo11.vinilos.repositories

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.network.NetworkServiceAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface IAlbumRepository {
    fun getAlbums(): Flow<List<Album>?>
    suspend fun refresh()
}

class AlbumRepository : IAlbumRepository {
    @Volatile
    private var deferredUntilRefresh = CompletableDeferred<Unit>()
    private val adapter = NetworkServiceAdapter()

    override fun getAlbums(): Flow<List<Album>?> = flow {
        while (true) {
            adapter.getAlbums().catch { err ->
                Log.e(TAG, "Error loading albums: $err")
                emit(null)
            }.collect { albums ->
                emit(albums)
            }

            deferredUntilRefresh.await()
            deferredUntilRefresh = CompletableDeferred()
        }
    }

    override suspend fun refresh() {
        deferredUntilRefresh.complete(Unit)
    }

    companion object {
        private val TAG = AlbumRepository::class.simpleName!!
    }
}
