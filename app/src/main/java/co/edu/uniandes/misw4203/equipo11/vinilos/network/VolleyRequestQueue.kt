package co.edu.uniandes.misw4203.equipo11.vinilos.network

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.NoCache

object VolleyRequestQueue {
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

    fun <T> addToRequestQueue(req: Request<T>): Request<T> {
        return requestQueue.add(req)
    }
}
