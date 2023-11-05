package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.Instant

class PerformerAdapter : TypeAdapter<Performer>() {
    override fun write(writer: JsonWriter, value: Performer?) {
        TODO("Not yet implemented")
    }

    override fun read(reader: JsonReader): Performer {
        var id: Int? = null
        var birthDate: Instant? = null
        var performerName: String? = null
        var image: String? = null
        var description: String? = null
        var type: PerformerType? = null

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = reader.nextInt()
                "name" -> performerName = reader.nextString()
                "image" -> image = reader.nextString()
                "description" -> description = reader.nextString()
                "creationDate" -> {
                    birthDate = Instant.parse(reader.nextString())
                    type = PerformerType.BAND
                }
                "birthDate" -> {
                    birthDate = Instant.parse(reader.nextString())
                    type = PerformerType.MUSICIAN
                }
                else -> reader.skipValue()
            }
        }
        reader.endObject()

        return Performer(
            requireNotNull(id),
            requireNotNull(type),
            requireNotNull(performerName),
            requireNotNull(image),
            requireNotNull(description),
            requireNotNull(birthDate),
        )
    }
}
