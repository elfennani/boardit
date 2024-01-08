package com.elfennani.boardit.data.models

sealed class Attachment(
    open val id: Int,
    open val url: String
) {
    data object Unsupported : Attachment(id = -1, url = "")
    data class Image(
        override val id: Int,
        override val url: String,
        val width: Int,
        val height: Int
    ) : Attachment(id, url)
}
