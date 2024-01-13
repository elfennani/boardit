package com.elfennani.boardit.data.models
data class Attachment(
    val id: Int,
    val url: String,
    val fileName: String,
    val type: AttachmentType
)