package com.elfennani.boardit.data.models

import android.net.Uri

sealed class EditorAttachment {
    data class Remote(val attachment: Attachment) : EditorAttachment()
    data class Local(val uri: Uri, val type: AttachmentType): EditorAttachment()
}

fun Attachment.toEditorAttachment() = EditorAttachment.Remote(this)