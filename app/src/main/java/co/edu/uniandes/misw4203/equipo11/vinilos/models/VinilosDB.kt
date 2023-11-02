package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Album::class,
        Band::class,
        Collector::class,
        CollectorPerformerCrossRef::class,
        Musician::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VinilosDB : RoomDatabase() {
    abstract fun albumDao(): AlbumDAO
    abstract fun collectorDao(): CollectorDAO

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
