package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity

@Entity(primaryKeys = ["trackId", "albumId"])
data class TrackAlbum(
    val trackId: Int,
    val albumId: Int,
)