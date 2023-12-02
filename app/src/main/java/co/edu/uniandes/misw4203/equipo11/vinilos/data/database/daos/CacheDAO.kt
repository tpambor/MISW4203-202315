package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Cache
import java.time.Instant

@Dao
abstract class CacheDAO {
    @Query("SELECT lastUpdate FROM Cache WHERE entity == :entity")
    abstract suspend fun getLastUpdate(entity: String): Instant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun setLastUpdate(cache: Cache)
}
