package com.elfennani.boardit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType

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

fun AttachmentEntity.asExternalModel(): Attachment = Attachment(
    id,
    url,
    fileName,
    when {
        mime.startsWith("image/") -> AttachmentType.Image(checkNotNull(width), checkNotNull(height))
        mime == "application/pdf" -> AttachmentType.Pdf
        mime == "other/link" -> AttachmentType.Link
        else -> AttachmentType.Unsupported
    }
)