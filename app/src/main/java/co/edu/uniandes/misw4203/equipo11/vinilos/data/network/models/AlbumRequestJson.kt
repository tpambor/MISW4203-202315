package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

data class AlbumRequestJson(
    val name: String,
    val cover: String,
    val releaseDate: String,
    val description: String,
    val genre: String,
    val recordLabel: String,
)
