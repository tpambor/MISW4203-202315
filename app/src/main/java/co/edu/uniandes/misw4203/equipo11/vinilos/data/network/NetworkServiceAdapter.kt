package co.edu.uniandes.misw4203.equipo11.vinilos.data.network

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorWithPerformers
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJSON
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters.InstantAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters.PerformerAdapter
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant


class NetworkServiceAdapter {
    companion object {
        const val API_BASE_URL = "https://misw4203-vinilos-back-prod-36e2c70f7f9e.herokuapp.com"
    }

    fun getAlbums(): Flow<List<Album>> {
        return HttpRequestQueue.get("$API_BASE_URL/albums").map { response ->
            GsonBuilder()
                .registerTypeAdapter(Instant::class.java, InstantAdapter())
                .create().fromJson(response, Array<Album>::class.java).toList()
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
