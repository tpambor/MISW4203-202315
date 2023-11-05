package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Album(
    @PrimaryKey val id: Int,
    val name: String,
    val cover: String,
    val releaseDate: Instant,
    val description: String,
    val genre: String,
    val recordLabel: String,
)
