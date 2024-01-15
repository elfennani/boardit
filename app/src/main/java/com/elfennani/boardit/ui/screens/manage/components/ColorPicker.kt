package com.elfennani.boardit.ui.screens.manage.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    color: Color,
    onPick: (Color) -> Unit
) {
    val colors = listOf<Color>(
        Color.Gray,
        Color(0xFF3B82F6),
        Color(0xFF8B5CF6),
        Color(0xFFEF4444),
        Color(0xFFF59E0B),
        Color(0xFF84CC16),
        Color(0xFF22C55E),
        Color(0xFF14B8A6),
        Color(0xFFD946EF)
    )

    Text(
        text = "Color",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .fillMaxWidth()
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(colors) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(it)
                    .aspectRatio(1f)
                    .clickable { onPick(it) },
                contentAlignment = Alignment.Center
            ) {
                if (it == color) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}