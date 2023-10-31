package co.edu.uniandes.misw4203.equipo11.vinilos.network

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache
import com.android.volley.toolbox.StringRequest
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.cancellation.CancellationException

object HttpRequestQueue {
    private val requestQueue: RequestQueue by lazy {
        val cache = NoCache()

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }

        requestQueue
    }

    fun get(url: String): Flow<String> = callbackFlow {
        val stringRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                trySendBlocking(response)
                channel.close()
            },
            { err -> cancel(CancellationException(err)) }
        )

        val request = requestQueue.add(stringRequest)

        awaitClose { request.cancel() }
    }
}
