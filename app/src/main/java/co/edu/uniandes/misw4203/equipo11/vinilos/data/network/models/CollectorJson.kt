package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer

data class CollectorJSON (
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    val favoritePerformers: List<Performer>
)
