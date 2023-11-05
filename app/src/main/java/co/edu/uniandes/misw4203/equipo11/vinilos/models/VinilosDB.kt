package co.edu.uniandes.misw4203.equipo11.vinilos.models

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Album::class,
        Collector::class,
        CollectorFavoritePerformer::class,
        Performer::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class VinilosDB : RoomDatabase() {
    abstract fun albumDao(): AlbumDAO
    abstract fun performerDao(): PerformerDAO
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
