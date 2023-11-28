package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity

@Entity(primaryKeys = ["collectorId", "albumId"])
data class CollectorAlbumCrossRef(
    val collectorId: Int,
    val albumId: Int,
    val price: Int,
    val status: CollectorAlbumStatus
)
