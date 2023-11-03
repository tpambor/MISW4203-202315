package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collector(
    @PrimaryKey val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    //val favoritePerformer: List<Performer>
)
