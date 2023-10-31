package co.edu.uniandes.misw4203.equipo11.vinilos.network

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Album
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.google.gson.Gson
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.cancellation.CancellationException

class NetworkServiceAdapter {
    companion object {
        const val API_BASE_URL = "https://misw4203-vinilos-back-dev-f134d6283b6e.herokuapp.com"
    }

    fun getAlbums(): Flow<List<Album>> = callbackFlow {
        val stringRequest = StringRequest(
            Request.Method.GET,
            "$API_BASE_URL/albums",
            { response ->
                val gson = Gson()
                val albums = gson.fromJson(response, Array<Album>::class.java).toList()

                trySendBlocking(albums)
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
