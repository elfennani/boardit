package com.elfennani.boardit.ui.screens.home.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.formatReadable
import com.elfennani.boardit.ui.theme.BoarditTheme
import java.time.LocalDateTime
import java.util.UUID

@SuppressLint("SimpleDateFormat")
@Composable
fun BoardItem(
    modifier: Modifier = Modifier,
    board: Board,
    onClick: (Board) -> Unit = {},
) {
    val context = LocalContext.current

    Row(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .clickable { onClick(board) }
            .padding(12.dp)
            .height(IntrinsicSize.Min)
        ,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (board.attachments.isNotEmpty()) {
            AsyncImage(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surface),
//                model = board.attachments[0].url,
                model = ImageRequest.Builder(context)
                    .data(board.attachments[0].url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Image,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = board.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = board.date.formatReadable(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                board.tags.filterIndexed { index, _ -> index < 3 }.forEach { TagCard(it) }
            }
        }
    }
}

@Composable
private fun TagCard(tag: Tag) {
    Box(
        Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(tag.color.copy(alpha = 0.2f))
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(text = tag.label, style = MaterialTheme.typography.labelSmall, color = tag.color)
    }
}

@Preview
@Composable
fun BoardPreview() {
    BoarditTheme {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp)
                .width(350.dp)
        ) {
            BoardItem(
                board = Board(
                    id = UUID.randomUUID().toString(),
                    title = "Hello World",
                    category = Category(UUID.randomUUID().toString(), "Haha"),
                    date = LocalDateTime.now(),
                    note = null
                )
            )
        }
    }
}