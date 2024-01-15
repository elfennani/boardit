package com.elfennani.boardit.data.local.models

import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import kotlinx.serialization.Serializable

@Serializable
data class SerializableAttachment(
    val id: String,
    val filename: String,
    val uri: String,
    val type: String,
    val width: Int?,
    val height: Int?
)

fun SerializableAttachment.asExternalModel() = Attachment(
    id,
    uri,
    filename,
    type = when (type) {
        "pdf" -> AttachmentType.Pdf
        "image" -> AttachmentType.Image(width!!, height!!)
        "link" -> AttachmentType.Link
        else -> AttachmentType.Unsupported
    }
)
