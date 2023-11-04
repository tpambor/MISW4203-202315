package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectorDAO {
    @Query("SELECT * FROM collector")
    fun getCollectors(): Flow<List<Collector>>

    @Insert
    suspend fun insertCollectors(collectors: List<Collector>)

    @Query("DELETE FROM collector")
    suspend fun deleteCollectors()
    @Transaction
    suspend fun deleteAndInsertCollectors(collectors: List<Collector>) {
        deleteCollectors()
        insertCollectors(collectors)
    }

}