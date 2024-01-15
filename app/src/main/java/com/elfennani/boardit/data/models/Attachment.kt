package com.elfennani.boardit.data.models

import com.elfennani.boardit.data.local.models.SerializableAttachment
import java.util.UUID

data class Attachment(
    val id: String = UUID.randomUUID().toString(),
    val url: String,
    val fileName: String,
    val type: AttachmentType
)

fun Attachment.serialize() = SerializableAttachment(
    id,
    fileName,
    url,
    type = when(type){
        is AttachmentType.Image -> "image"
        is AttachmentType.Link -> "link"
        is AttachmentType.Pdf -> "pdf"
        else -> "unsupported"
    },
    width = if(type is AttachmentType.Image) type.width else null,
    height = if(type is AttachmentType.Image) type.height else null,
)