package com.elfennani.boardit.data.models

import kotlinx.serialization.Serializable

@Serializable
sealed class AttachmentType {
    data class Image(val width: Int, val height: Int): AttachmentType()
    data object Pdf: AttachmentType()
    data object Link: AttachmentType()
    data object Unsupported: AttachmentType()
}