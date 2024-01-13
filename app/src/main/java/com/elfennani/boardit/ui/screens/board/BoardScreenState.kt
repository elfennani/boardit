package com.elfennani.boardit.ui.screens.board

import androidx.compose.runtime.Immutable
import com.elfennani.boardit.data.local.entities.CachedAttachmentEntity
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.Board

@Immutable
data class BoardScreenState(
    val board: Board? = null,
    val currentlyDownloadingAttachments: List<Attachment> = emptyList(),
    val downloadedAttachments: List<CachedAttachmentEntity> = emptyList()
)
