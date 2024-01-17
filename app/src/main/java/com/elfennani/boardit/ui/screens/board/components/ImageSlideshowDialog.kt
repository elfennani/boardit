package com.elfennani.boardit.ui.screens.board.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageSlideshowDialog(
    dialogOpen: Boolean,
    onDismiss: () -> Unit,
    imageAttachments: List<Attachment>,
    initialPage: Int
) {
    if (dialogOpen) {
        Dialog(
            onDismissRequest = onDismiss,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val dialogPagerState = rememberPagerState(
                initialPage = initialPage,
                initialPageOffsetFraction = 0f
            ) { imageAttachments.size }

            HorizontalPager(state = dialogPagerState, modifier = Modifier.fillMaxSize()) {
                val attachment = imageAttachments[it]
                val type = attachment.type as AttachmentType.Image
                val zoomState = rememberZoomState(
                    contentSize = Size(
                        type.width.toFloat(),
                        type.height.toFloat()
                    )
                )

                AsyncImage(
                    model = attachment.url,
                    contentDescription = null,
                    modifier = Modifier
                        .zoomable(zoomState)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}