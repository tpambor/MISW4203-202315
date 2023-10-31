package co.edu.uniandes.misw4203.equipo11.vinilos.network

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NetworkServiceAdapter {
    companion object {
        const val API_BASE_URL = "https://misw4203-vinilos-back-dev-f134d6283b6e.herokuapp.com"
    }

    fun getAlbums(): Flow<List<Album>> {
        return HttpRequestQueue.get("$API_BASE_URL/albums").map { response ->
            Gson().fromJson(response, Array<Album>::class.java).toList()
        }
    }
}
