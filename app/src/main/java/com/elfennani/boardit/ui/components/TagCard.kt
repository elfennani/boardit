package com.elfennani.boardit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.data.models.Tag

@Composable
fun TagCard(tag: Tag, onClick: (() -> Unit)? = null) {
    Row(
        Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
            .background(tag.color.copy(alpha = 0.2f))
            .padding(vertical = 4.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = tag.label, style = MaterialTheme.typography.labelSmall, color = tag.color)
        if (onClick != null) {
            Icon(imageVector = Icons.Rounded.Close, contentDescription = null, tint = tag.color, modifier = Modifier.size(14.dp))
        }
    }
}