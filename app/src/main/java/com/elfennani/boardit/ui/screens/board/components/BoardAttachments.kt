package com.elfennani.boardit.ui.screens.board.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType
import com.elfennani.boardit.data.models.LinkMetadata
import com.elfennani.boardit.ui.screens.board.BoardScreenState
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import java.io.File


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardAttachments(
    attachments: List<Pair<Attachment, LinkMetadata?>>,
    onDialogState: (BoardScreenState.DialogState) -> Unit,
    dialogState: BoardScreenState.DialogState,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { attachments.size }

    if (dialogState is BoardScreenState.DialogState.Open) {
        Dialog(
            onDismissRequest = { onDialogState(BoardScreenState.DialogState.Closed) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val imageAttachments = attachments.filter { it.first.type is AttachmentType.Image }
            val dialogPagerState = rememberPagerState(
                initialPage = imageAttachments.indexOf(attachments[dialogState.initialIndex]),
                initialPageOffsetFraction = 0f
            ) { imageAttachments.size }

            HorizontalPager(state = dialogPagerState, modifier = Modifier.fillMaxSize()) {
                val attachment = imageAttachments[it].first

                if (attachment.type is AttachmentType.Image)
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(attachment.url)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .zoomable(
                                rememberZoomState(
                                    contentSize = Size(
                                        attachment.type.width.toFloat(),
                                        attachment.type.height.toFloat()
                                    )
                                )
                            )
                            .fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
            }
        }
    }

//    val aspectRatio =
//        attachments.filter { it.first.type is AttachmentType.Image }
//            .maxOfOrNull { (it.first.type as AttachmentType.Image).width.toFloat() / (it.first.type as AttachmentType.Image).height.toFloat() }
//            ?: (16f / 9)
    val aspectRatio = 16f/9

    val openFile: (String) -> Unit = {
        val newIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        newIntent.setDataAndType(
            FileProvider.getUriForFile(
                context,
                context.packageName + ".provider",
                File(it)
            ), "application/pdf"
        )
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(context, newIntent, bundleOf())
        } catch (e: Exception) {
            Log.e("INTENT", e.toString())
        }
    }

    val openLink: (String) -> Unit = {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
        startActivity(context, browserIntent, bundleOf())
    }

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

            when (attachment.type) {
                is AttachmentType.Image -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(attachment.url)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .clickable { onDialogState(BoardScreenState.DialogState.Open(index)) }
                            .aspectRatio(aspectRatio),
                        contentScale = ContentScale.Crop
                    )
                }

                is AttachmentType.Pdf -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = null)
                        Button(onClick = {
                            openFile(attachment.url)
                        }) {
                            Text(text = "Open")
                        }
                    }
                }

                is AttachmentType.Link -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = metadata?.thumbnailUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )


                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp)
                                .padding(bottom = 24.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(8.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Link,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary
                                )
                                Column {
                                    Text(
                                        text = metadata?.title ?: attachment.url,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodySmall,
                                        overflow = TextOverflow.Ellipsis,
                                        fontWeight = FontWeight.Medium
                                    )
                                    if(!metadata?.title.isNullOrBlank()) Text(
                                        text = attachment.url,
                                        maxLines = 1,
                                        style = MaterialTheme.typography.labelSmall,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }

                else -> {}
            }
        }

        if (attachments.size > 1)
            Text(
                text = "${pagerState.currentPage + 1} / ${attachments.size}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(16.dp, 16.dp)
                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            )

        if (attachments.size > 1)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-16).dp)
                    .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(100.dp))
                    .padding(horizontal = 4.dp)
                    .height(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(attachments.size) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (it == pagerState.currentPage) Color.White else Color.White.copy(
                                    alpha = 0.75f
                                )
                            )
                            .animateContentSize()
                            .size(if (it == pagerState.currentPage) 6.dp else 4.dp)
                    )
                }
            }
    }
}
