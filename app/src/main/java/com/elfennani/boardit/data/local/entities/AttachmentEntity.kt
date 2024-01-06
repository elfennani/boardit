package com.elfennani.boardit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.elfennani.boardit.data.models.Attachment

@Entity("attachment")
data class AttachmentEntity(
    @PrimaryKey val id: Int,
    val fileName: String,
    val url: String,
    val userId: String,
    val boardId: Int
)

fun AttachmentEntity.asExternalModel() = Attachment(
    id,
    url
)