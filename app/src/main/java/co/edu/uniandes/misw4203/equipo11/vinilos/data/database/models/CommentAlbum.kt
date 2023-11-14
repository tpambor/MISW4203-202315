package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity

@Entity(primaryKeys = ["CommentId", "albumId"])
data class CommentAlbum(
    val trackId: Int,
    val albumId: Int,
)