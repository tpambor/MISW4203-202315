package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Comment(
    @PrimaryKey val id: Int,
    val description: String,
    val rating: Int,
    val albumId: Int,
)