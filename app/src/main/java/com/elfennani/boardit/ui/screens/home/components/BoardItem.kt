package com.elfennani.boardit.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.ui.theme.BoarditTheme

@Composable
fun BoardItem(
    board: Board
) {
    val tags = listOf<Tag>(
        Tag(0, "Textures", Color(0xFFEF4444)),
        Tag(0, "Colors", Color(0xFF22C55E)),
        Tag(0, "Themes", Color(0xFF8B5CF6))
    )

    Row(
        Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .fillMaxWidth()
            .clickable {  }
            .padding(12.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Image,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(32.dp)
            )
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
                    text = "May 5, 2023 at 6:45 PM",
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
                Board(
                    id = 0,
                    title = "Hello World",
                    category = Category(0, "Haha"),
                    note = null
                )
            )
        }
    }
}