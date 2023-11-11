package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity

@Entity(primaryKeys = ["performerId", "albumId"])
data class PerformerAlbum(
    val performerId: Int,
    val albumId: Int,
)
