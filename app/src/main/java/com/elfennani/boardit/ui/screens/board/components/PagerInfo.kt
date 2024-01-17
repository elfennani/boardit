package com.elfennani.boardit.ui.screens.board.components


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.PagerInfo(
    isVisible: Boolean,
    index: Int,
    total: Int,
) {
    if (isVisible) {
        Text(
            text = "${index + 1} / $total",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(16.dp, 16.dp)
                .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(100.dp))
                .padding(horizontal = 6.dp, vertical = 3.dp)
        )

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
            repeat(total) {
                val color = if (it == index) Color.White else Color.White.copy(alpha = 0.75f)
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color)
                        .animateContentSize()
                        .size(if (it == index) 6.dp else 4.dp)
                )
            }
        }
    }
}
