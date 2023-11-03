package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

enum class PerformerType(val value: Int) {
    MUSICIAN(0),
    BAND(1),
}

@Entity
data class Performer(
    @PrimaryKey val id: Int,
    val type: PerformerType,
    val name: String,
    val image: String,
    val description: String,
    val birthDate: Instant, // creationDate for bands
)
