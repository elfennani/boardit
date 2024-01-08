package com.elfennani.boardit.ui.screens.board.components

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.elfennani.boardit.data.models.Attachment
import kotlin.math.atan

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoardAttachments(
    attachments: List<Attachment>
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { attachments.size }

    val aspectRatio =
        attachments.maxOf { (it as Attachment.Image).width.toFloat() / it.height.toFloat() }
    Log.d("ASPECTRATIO", attachments.toString())

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
    ) {
        HorizontalPager(state = pagerState) {
            val attachment = attachments[it] as Attachment.Image

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(attachment.url)
                    .build(),
                contentDescription = null,
                modifier = Modifier.aspectRatio(aspectRatio),
                contentScale = ContentScale.Crop
            )
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