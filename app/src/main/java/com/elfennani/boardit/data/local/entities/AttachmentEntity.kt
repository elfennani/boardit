package com.elfennani.boardit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.DataType

@Entity("attachment")
data class AttachmentEntity(
    @PrimaryKey val id: Int,
    val fileName: String,
    val url: String,
    val userId: String,
    val boardId: Int,
    val mime: String,
    val width: Int?,
    val height: Int?,
)

fun AttachmentEntity.asExternalModel(): Attachment = when {
    mime.startsWith("image/") -> Attachment.Image(
        id,
        url,
        DataType.REMOTE,
        checkNotNull(width),
        checkNotNull(height)
    )
    else -> Attachment.Unsupported
}