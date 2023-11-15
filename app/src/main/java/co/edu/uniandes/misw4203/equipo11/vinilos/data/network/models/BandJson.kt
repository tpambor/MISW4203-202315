package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

import java.time.Instant

data class BandJson(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val creationDate: Instant,
    val albums: List<AlbumJson>?,
    val musicians: List<MusicianJson>?,
    val collectors: List<CollectorJson>?
)
