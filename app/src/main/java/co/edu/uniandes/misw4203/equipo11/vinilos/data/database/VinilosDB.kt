package co.edu.uniandes.misw4203.equipo11.vinilos.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos.AlbumDAO
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos.CollectorDAO
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.daos.PerformerDAO
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorFavoritePerformer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.MusicianBand
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerAlbum
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track

@Database(
    entities = [
        Album::class,
        Collector::class,
        CollectorFavoritePerformer::class,
        Comment::class,
        MusicianBand::class,
        Performer::class,
        PerformerAlbum::class,
        Track::class
    ],
    version = 8,
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
