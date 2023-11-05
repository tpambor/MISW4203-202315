package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Performer(
    @PrimaryKey val id: Int,
    val type: PerformerType,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: Instant, // creationDate for bands
)
