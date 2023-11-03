package co.edu.uniandes.misw4203.equipo11.vinilos.network

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.models.CollectorWithPerformers
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

    fun getMusicians(): Flow<List<Performer>> {
        return HttpRequestQueue.get("$API_BASE_URL/musicians").map { response ->
            GsonBuilder()
                .registerTypeAdapter(Performer::class.java, PerformerAdapter())
                .create().fromJson(response, Array<Performer>::class.java).toList()
        }
    }

    fun getBands(): Flow<List<Performer>> {
        return HttpRequestQueue.get("$API_BASE_URL/bands").map { response ->
            GsonBuilder()
                .registerTypeAdapter(Performer::class.java, PerformerAdapter())
                .create().fromJson(response, Array<Performer>::class.java).toList()
        }
    }

    fun getCollectors(): Flow<List<CollectorWithPerformers>> {
        return HttpRequestQueue.get("$API_BASE_URL/collectors").map { response ->
            GsonBuilder()
                .registerTypeAdapter(Performer::class.java, PerformerAdapter())
                .create().fromJson(response, Array<CollectorJSON>::class.java).toList()
        } .map { collectors ->
            collectors.map {
                // Transform CollectorJSON to internal representation CollectorWithPerformers
                val c = Collector(it.id, it.name, it.telephone, it.email)
                CollectorWithPerformers(c, it.favoritePerformers)
            }
        }
    }
}
