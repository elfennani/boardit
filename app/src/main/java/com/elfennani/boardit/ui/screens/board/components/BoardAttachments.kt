package com.elfennani.boardit.ui.screens.board.components

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.os.bundleOf
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.elfennani.boardit.data.local.entities.CachedAttachmentEntity
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.AttachmentType

private fun Attachment.getUrl(cached: List<CachedAttachmentEntity>): String {
    val cachedUrl = cached.firstOrNull { it.url == this.url }

    return cachedUrl?.uri?.toString() ?: this.url
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardAttachments(
    attachments: List<Attachment>,
    currentlyDownloading: List<Attachment>,
    cached: List<CachedAttachmentEntity>,
    onRequestAttachment: (Attachment) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { attachments.size }

    val aspectRatio =
        attachments.filter { it.type is AttachmentType.Image }
            .maxOfOrNull { (it.type as AttachmentType.Image).width.toFloat() / it.type.height.toFloat() }
            ?: (16f / 9)

    val openPdf: (String) -> Unit = {
        val newIntent = Intent(Intent.ACTION_VIEW)
        newIntent.data = Uri.parse(it)
        newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        try {
            startActivity(context, newIntent, bundleOf())
        } catch (e: Exception) {
            Log.e("INTENT", e.toString())
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ) {
        HorizontalPager(state = pagerState) { index ->
            val attachment = attachments[index]

            LaunchedEffect(key1 = null) {
                if (
                    cached.firstOrNull { it.url == attachment.url } == null &&
                    !currentlyDownloading.contains(attachment) &&
                    attachment.type !is AttachmentType.Link
                ) {
                    onRequestAttachment(attachment)
                }
            }

            when (attachment.type) {
                is AttachmentType.Image -> {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(attachment.getUrl(cached))
                            .build(),
                        contentDescription = null,
                        modifier = Modifier.aspectRatio(aspectRatio),
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
                            openPdf(attachment.getUrl(cached))
                        }) {
                            Text(text = "Open")
                        }
                    }
                }

                is AttachmentType.Link -> {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Rounded.Link, contentDescription = null)
                        Button(onClick = {
                            openPdf(attachment.url)
                        }) {
                            Text(text = "Open Link")
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
