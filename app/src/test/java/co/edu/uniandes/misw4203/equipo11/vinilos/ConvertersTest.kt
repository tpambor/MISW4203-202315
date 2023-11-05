package co.edu.uniandes.misw4203.equipo11.vinilos

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.Converters
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class ConvertersTest {
    @Test
    fun shouldReturnSameDate() {
        val converters = Converters()

        val date = Instant.now().truncatedTo(ChronoUnit.SECONDS)
        assertEquals(date, converters.fromTimestamp(converters.dateToTimestamp(date)))
    }
}
