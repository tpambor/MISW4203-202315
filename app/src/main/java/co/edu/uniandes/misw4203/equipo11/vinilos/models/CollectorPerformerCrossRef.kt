package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Entity

@Entity(primaryKeys = ["collectorId", "performerId"])
data class CollectorPerformerCrossRef(
    val collectorId: Int,
    val performerId: Int
)
