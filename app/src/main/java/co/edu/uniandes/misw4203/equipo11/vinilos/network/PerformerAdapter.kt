package co.edu.uniandes.misw4203.equipo11.vinilos.network

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.models.PerformerType
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PerformerAdapter : TypeAdapter<Performer>() {
    override fun write(writer: JsonWriter, value: Performer?) {
        TODO("Not yet implemented")
    }

    override fun read(reader: JsonReader): Performer {
        var id: Int? = null
        var birthDate: Date? = null
        var performerName: String? = null
        var image: String? = null
        var description: String? = null
        var type: PerformerType? = null

        val df = SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

        reader.beginObject()
        while (reader.hasNext()) {
            when (reader.nextName()) {
                "id" -> id = reader.nextInt()
                "name" -> performerName = reader.nextString()
                "image" -> image = reader.nextString()
                "description" -> description = reader.nextString()
                "creationDate" -> {
                    birthDate = df.parse(reader.nextString())
                    type = PerformerType.BAND
                }
                "birthDate" -> {
                    birthDate = df.parse(reader.nextString())
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
