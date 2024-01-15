package com.elfennani.boardit.data.models

import android.net.Uri

enum class NewAttachmentType{
    IMAGE, PDF, LINK
}

data class NewAttachment(val uri: Uri, val type: NewAttachmentType)