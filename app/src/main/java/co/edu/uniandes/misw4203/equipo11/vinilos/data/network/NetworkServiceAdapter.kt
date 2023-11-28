package co.edu.uniandes.misw4203.equipo11.vinilos.data.network

import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJsonRequest
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJsonResponse
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CommentJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.PerformerJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters.InstantAdapter
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters.PerformerDeserializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class NetworkServiceAdapter {
    companion object {
        const val API_BASE_URL = "https://misw4203-vinilos-back-dev-f134d6283b6e.herokuapp.com"

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

    fun getAlbum(albumId: Int): Flow<AlbumJson> {
        return HttpRequestQueue.get("$API_BASE_URL/albums/$albumId").map { response ->
            gson().fromJson(response, AlbumJson::class.java)
        }
    }
    fun insertAlbum(album: AlbumJsonRequest): Flow<AlbumJsonResponse> {
        val gson = Gson()
        val albumJson = gson.toJson(album)
        return HttpRequestQueue.post("$API_BASE_URL/albums", albumJson.toString()).map { response ->
            gson().fromJson(response,  AlbumJsonResponse::class.java)
        }
    }
    fun getMusicians(): Flow<List<MusicianJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/musicians").map { response ->
            gson().fromJson(response, Array<MusicianJson>::class.java).toList()
        }
    }

    fun getMusician(musicianId: Int): Flow<MusicianJson> {
        return HttpRequestQueue.get("$API_BASE_URL/musicians/$musicianId").map { response ->
            gson().fromJson(response, MusicianJson::class.java)
        }
    }

    fun getBands(): Flow<List<BandJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/bands").map { response ->
            gson().fromJson(response, Array<BandJson>::class.java).toList()
        }
    }

    fun getBand(musicianId: Int): Flow<BandJson> {
        return HttpRequestQueue.get("$API_BASE_URL/bands/$musicianId").map { response ->
            gson().fromJson(response, BandJson::class.java)
        }
    }

    fun getCollectors(): Flow<List<CollectorJson>> {
        return HttpRequestQueue.get("$API_BASE_URL/collectors").map { response ->
            gson().fromJson(response, Array<CollectorJson>::class.java).toList()
        }
    }

    fun getCollector(collectorId: Int): Flow<CollectorJson> {
        return HttpRequestQueue.get("$API_BASE_URL/collectors/$collectorId").map { response ->
            gson().fromJson(response, CollectorJson::class.java)
        }
    }

    fun addFavoriteMusicianToCollector(collectorId: Int, musicianId: Int): Flow<MusicianJson> {
        return HttpRequestQueue.post("$API_BASE_URL/collectors/$collectorId/musicians/$musicianId", "").map { response ->
            gson().fromJson(response, MusicianJson::class.java)
        }
    }

    fun addFavoriteBandToCollector(collectorId: Int, musicianId: Int): Flow<BandJson> {
        return HttpRequestQueue.post("$API_BASE_URL/collectors/$collectorId/bands/$musicianId", "").map { response ->
            gson().fromJson(response, BandJson::class.java)
        }
    }

    fun removeFavoriteMusicianFromCollector(collectorId: Int, musicianId: Int): Flow<Unit> {
        return HttpRequestQueue.delete("$API_BASE_URL/collectors/$collectorId/musicians/$musicianId").map {}
    }

    fun removeFavoriteBandFromCollector(collectorId: Int, bandId: Int): Flow<Unit> {
        return HttpRequestQueue.delete("$API_BASE_URL/collectors/$collectorId/bands/$bandId").map {}
    }

    fun addCommentToAlbum(albumId: Int, collectorId: Int, rating: Int, comment: String): Flow<CommentJson> {
        val json = JsonObject()
        json.addProperty("description", comment)
        json.addProperty("rating", rating)
        val collector = JsonObject()
        collector.addProperty("id", collectorId)
        json.add("collector", collector)

        return HttpRequestQueue.post("$API_BASE_URL/albums/$albumId/comments", json.toString()).map { response ->
            gson().fromJson(response, CommentJson::class.java)
        }
    }
}
