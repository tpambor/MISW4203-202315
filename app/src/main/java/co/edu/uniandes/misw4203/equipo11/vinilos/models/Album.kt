package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey val id: Int,
    val name: String,
    val genre: String,
    val cover: String
)
