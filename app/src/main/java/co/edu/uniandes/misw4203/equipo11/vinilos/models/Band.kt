package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Band(
    @PrimaryKey override val id: Int,
    override val name: String,
    override val image: String,
    override val description: String,
    val creationDate: Date,
) : Performer()
