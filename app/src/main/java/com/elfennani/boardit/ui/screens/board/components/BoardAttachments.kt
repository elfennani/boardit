package com.elfennani.boardit.ui.screens.board.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.LinkMetadata
import com.elfennani.boardit.ui.screens.board.BoardScreenState


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardAttachments(
    attachments: List<Pair<Attachment, LinkMetadata?>>,
    onDialogState: (BoardScreenState.DialogState) -> Unit,
    dialogState: BoardScreenState.DialogState,
) {
    val aspectRatio = 16f / 9
    val pagerState = rememberPagerState(0, 0f) {
        attachments.size
    }

    val imageAttachments = attachments.filter { it.first.type is AttachmentType.Image }
    val initialIndex = when (dialogState) {
        is BoardScreenState.DialogState.Open -> dialogState.initialIndex
        else -> 0
    }

    ImageSlideshowDialog(
        dialogOpen = dialogState is BoardScreenState.DialogState.Open,
        onDismiss = { onDialogState(BoardScreenState.DialogState.Closed) },
        imageAttachments = imageAttachments.map { it.first },
        initialPage = imageAttachments.indexOf(attachments[initialIndex])
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ) {
        HorizontalPager(
            state = pagerState,
        ) { index ->
            val attachment = attachments[index].first
            val metadata = attachments[index].second
            val onOpenImage = { onDialogState(BoardScreenState.DialogState.Open(index)) }

            when (attachment.type) {
                is AttachmentType.Image -> ImageAttachment(attachment.url, onOpenImage)
                is AttachmentType.Pdf -> PdfAttachment(attachment.url)
                is AttachmentType.Link -> LinkAttachment(
                    attachment.url,
                    metadata,
                    attachments.size == 1
                )
                else -> {}
            }
        }

        PagerInfo(
            isVisible = attachments.size > 1,
            index = pagerState.currentPage,
            total = attachments.size
        )
    }
}
