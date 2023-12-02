package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity
data class Cache(
    @PrimaryKey val entity: String,
    val lastUpdate: Instant
)
