package co.edu.uniandes.misw4203.equipo11.vinilos.data.database

import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Album
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Collector
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbumCrossRef
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.CollectorAlbumStatus
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Comment
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Performer
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.PerformerType
import co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models.Track
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.AlbumJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.BandJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorAlbumJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CollectorJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.CommentJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.MusicianJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.PerformerJson
import co.edu.uniandes.misw4203.equipo11.vinilos.data.network.models.TrackJson

fun AlbumJson.toAlbum(): Album {
    return Album(
        id = this.id,
        name = this.name,
        cover = this.cover,
        releaseDate = this.releaseDate,
        description = this.description,
        genre = this.genre,
        recordLabel = this.recordLabel
    )
}

fun MusicianJson.toPerformer(): Performer {
    return Performer(
        id = this.id,
        type = PerformerType.MUSICIAN,
        name = this.name,
        image = this.image,
        description = this.description,
        birthDate = this.birthDate
    )
}

fun BandJson.toPerformer(): Performer {
    return Performer(
        id = this.id,
        type = PerformerType.BAND,
        name = this.name,
        image = this.image,
        description = this.description,
        birthDate = this.creationDate
    )
}

fun PerformerJson.toPerformer(): Performer {
    if (this is PerformerJson.Musician) {
        return musician.toPerformer()
    }

    if (this is PerformerJson.Band) {
        return band.toPerformer()
    }

    throw UnsupportedOperationException()
}

fun CollectorJson.toCollector(): Collector {
    return Collector(
        id = this.id,
        name = this.name,
        telephone = this.telephone,
        email = this.email,
    )
}

fun CollectorAlbumJson.toCollectorAlbum(collectorId: Int): CollectorAlbumCrossRef {
    return CollectorAlbumCrossRef(
        collectorId = collectorId,
        albumId = this.id,
        price = this.price,
        status = if (this.status == "Active") CollectorAlbumStatus.Active else CollectorAlbumStatus.Inactive
    )
}

fun TrackJson.toTrack(albumId: Int): Track {
    return Track(
        id = this.id,
        name = this.name,
        duration = this.duration,
        albumId = albumId
    )
}

fun CommentJson.toComment(albumId: Int): Comment {
    return Comment(
        id = this.id,
        description = this.description,
        rating = this.rating,
        albumId = albumId
    )
}
