package co.edu.uniandes.misw4203.equipo11.vinilos

import co.edu.uniandes.misw4203.equipo11.vinilos.models.Converters
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class ConvertersTest {
    @Test
    fun shouldReturnSameDate() {
        val converters = Converters()

        val date = Date()
        assertEquals(date, converters.fromTimestamp(converters.dateToTimestamp(date)))
    }
}
