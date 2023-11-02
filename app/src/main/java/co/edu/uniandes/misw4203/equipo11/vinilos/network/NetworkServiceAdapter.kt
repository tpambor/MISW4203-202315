package co.edu.uniandes.misw4203.equipo11.vinilos.network

import android.util.Log
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Band
import co.edu.uniandes.misw4203.equipo11.vinilos.models.Musician
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlin.coroutines.cancellation.CancellationException

class NetworkServiceAdapter {
    companion object {
        const val API_BASE_URL = "https://misw4203-vinilos-back-dev-f134d6283b6e.herokuapp.com"
    }

    fun getAlbums(): Flow<List<Album>> {
        return HttpRequestQueue.get("$API_BASE_URL/albums").map { response ->
            Gson().fromJson(response, Array<Album>::class.java).toList()
        }
    }

    fun getMusicians(): Flow<List<Musician>> = callbackFlow {
        val stringRequest = StringRequest(
            Request.Method.GET,
            "$API_BASE_URL/musicians",
            { response ->
                val gson = Gson()
                val musicians = gson.fromJson(response, Array<Musician>::class.java).toList()
                trySendBlocking(musicians)
                channel.close()
            },
            { err ->
                cancel(CancellationException(err))
            }
        )

        val request = VolleyRequestQueue.addToRequestQueue(stringRequest)

        awaitClose { request.cancel() }
    }

    fun getBands(): Flow<List<Band>> = callbackFlow {
        val stringRequest = StringRequest(
            Request.Method.GET,
            "$API_BASE_URL/bands",
            { response ->
                val gson = Gson()
                val bands = gson.fromJson(response, Array<Band>::class.java).toList()
                Log.i("Bands", bands.toString())
                trySendBlocking(bands)
                channel.close()
            },
            { err ->
                cancel(CancellationException(err))
            }
        )

        val request = VolleyRequestQueue.addToRequestQueue(stringRequest)

        awaitClose { request.cancel() }
    }
}
