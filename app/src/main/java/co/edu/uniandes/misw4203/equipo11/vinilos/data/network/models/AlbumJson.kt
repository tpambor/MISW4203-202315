package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

import java.time.Instant

data class AlbumJson(
    val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: Instant,
    val description: String,
    val genre: String,
    val recordLabel: String,
)
