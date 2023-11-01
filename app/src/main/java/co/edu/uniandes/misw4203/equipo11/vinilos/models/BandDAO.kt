package co.edu.uniandes.misw4203.equipo11.vinilos.models

import android.telephony.AccessNetworkConstants.NgranBands
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BandDAO {
    @Query("SELECT * FROM band")
    fun getBands(): Flow<List<Band>>

    @Insert
    suspend fun insertBands(bands: List<Band>)

    @Query("DELETE FROM band")
    suspend fun deleteBands()

    @Transaction
    suspend fun deleteAndInsertBands(bands: List<Band>) {
        deleteBands()
        insertBands(bands)
    }
}