package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Embedded

data class CollectorAlbum(
    val collectorId: Int,
    @Embedded(prefix = "album_")
    val album: Album,
    val price: Int,
    val status: CollectorAlbumStatus
)
