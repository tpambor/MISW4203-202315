package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models

sealed interface PerformerJson {
    data class Musician(val musician: MusicianJson) : PerformerJson
    data class Band(val band: BandJson): PerformerJson
}
