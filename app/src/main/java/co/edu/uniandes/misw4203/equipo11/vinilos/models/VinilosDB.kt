package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        Album::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VinilosDB : RoomDatabase() {
    abstract fun albumDao(): AlbumDAO

    companion object {
        @Volatile
        private var INSTANCE: VinilosDB? = null

        fun setInstance(instance: VinilosDB) {
            synchronized(this) {
                INSTANCE = instance
            }
        }

        fun getInstance(): VinilosDB {
            synchronized(this) {
                return INSTANCE ?: throw IllegalStateException()
            }
        }
    }
}
