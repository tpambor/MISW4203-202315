package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

import java.time.Instant

data class MusicianJson(
    val id: Int,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: Instant,
    val albums: List<AlbumJson>?
)
