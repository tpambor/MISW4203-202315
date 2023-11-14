package co.edu.uniandes.misw4203.equipo11.vinilos.data.database.models

import androidx.room.Entity

@Entity(primaryKeys = ["commentId", "albumId"])
data class CommentAlbum(
    val commentId: Int,
    val albumId: Int,
)