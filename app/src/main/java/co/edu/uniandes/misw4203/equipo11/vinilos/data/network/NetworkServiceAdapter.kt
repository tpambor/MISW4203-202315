package co.edu.uniandes.misw4203.equipo11.vinilos.data.network

import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.PerformerJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters.InstantAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters.PerformerDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class NetworkServiceAdapter {
    companion object {
        const val API_BASE_URL = "https://misw4203-vinilos-back-prod-36e2c70f7f9e.herokuapp.com"

        private val gsonBuilder = GsonBuilder()
            .registerTypeAdapter(Instant::class.java, InstantAdapter())
            .registerTypeAdapter(PerformerJson::class.java, PerformerDeserializer())

        private fun gson() : Gson {
            return gsonBuilder.create()
        }
    }

    fun getAlbums(): Flow<List<AlbumJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/albums").map { response ->
            gson().fromJson(response, Array<AlbumJson>::class.java).toList()
        }
    }

    fun getMusicians(): Flow<List<MusicianJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/musicians").map { response ->
            gson().fromJson(response, Array<MusicianJson>::class.java).toList()
        }
    }

    fun getBands(): Flow<List<BandJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/bands").map { response ->
            gson().fromJson(response, Array<BandJson>::class.java).toList()
        }
    }

    fun getCollectors(): Flow<List<CollectorJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/collectors").map { response ->
            gson().fromJson(response, Array<CollectorJson>::class.java).toList()
        }
    }
}
