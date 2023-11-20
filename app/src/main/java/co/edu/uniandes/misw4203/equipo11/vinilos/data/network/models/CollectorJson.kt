package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

data class CollectorJson (
    val id: Int,
    val name: String,
    val telephone: String,
    val email: String,
    val favoritePerformers: List<PerformerJson>?
)
