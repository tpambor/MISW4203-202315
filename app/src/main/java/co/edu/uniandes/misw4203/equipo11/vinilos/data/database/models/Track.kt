package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track (
    @PrimaryKey val id: Int,
    val name: String,
    val duration: String,
    val albumId: Int
)