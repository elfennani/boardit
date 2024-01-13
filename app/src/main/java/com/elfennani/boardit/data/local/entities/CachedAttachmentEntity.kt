package com.elfennani.boardit.data.local.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("cached_attachment")
data class CachedAttachmentEntity(
    @PrimaryKey val url: String,
    val uri: Uri
)
