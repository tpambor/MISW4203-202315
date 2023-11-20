package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity

@Entity(primaryKeys = ["musicianId", "bandId"])
data class MusicianBand(
    val musicianId: Int,
    val bandId: Int
)
