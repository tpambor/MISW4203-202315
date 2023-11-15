package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters

import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.PerformerJson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class PerformerDeserializer : JsonDeserializer<PerformerJson> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PerformerJson {
        val obj = json.asJsonObject

        if (obj.has("birthDate")) {
            val musician = context.deserialize<MusicianJson>(json, MusicianJson::class.java)
            return PerformerJson.Musician(musician)
        }

        if (obj.has("creationDate")) {
            val band = context.deserialize<BandJson>(json, BandJson::class.java)
            return PerformerJson.Band(band)
        }

        throw UnsupportedOperationException()
    }
}
