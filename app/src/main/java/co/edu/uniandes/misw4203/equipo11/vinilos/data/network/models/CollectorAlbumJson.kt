package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

data class CollectorAlbumJson (
    val id: Int,
    val price: Int,
    val status: String,
    val album: AlbumJson
)
