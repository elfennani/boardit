package com.elfennani.boardit.data.models

sealed class Attachment(
    open val id: Int,
    open val url: String,
    open val dataType: DataType = DataType.REMOTE
) {
    data object Unsupported : Attachment(id = -1, url = "")
    data class Image(
        override val id: Int,
        override val url: String,
        override val dataType: DataType = DataType.REMOTE,
        val width: Int,
        val height: Int,
    ) : Attachment(id, url, dataType)
}
