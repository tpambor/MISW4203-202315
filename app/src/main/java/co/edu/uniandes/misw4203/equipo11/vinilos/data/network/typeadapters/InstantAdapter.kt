package co.edu.uniandes.misw4203.equipo11.vinilos.data.network.typeadapters

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.Instant

class InstantAdapter : TypeAdapter<Instant>() {
    override fun write(writer: JsonWriter, value: Instant?) {
        TODO("Not yet implemented")
    }

    override fun read(reader: JsonReader): Instant {
        return Instant.parse(reader.nextString())
    }
}
