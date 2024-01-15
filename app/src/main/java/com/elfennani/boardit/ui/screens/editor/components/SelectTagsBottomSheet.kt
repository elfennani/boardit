package com.elfennani.boardit.ui.screens.editor.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.models.getColor
import com.elfennani.boardit.ui.components.BottomSheet

@Composable
fun SelectTagsBottomSheet(
    tags: List<Tag>,
    selected: Set<Tag>,
    onSelect: (Tag) -> Unit,
    onClose: () -> Unit,
) {
    BottomSheet(
        onDismissRequest = onClose,
        title = "Select Category",
        onConfirm = { it() }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            tags.forEach { tag ->
                val isSelected = selected.contains(tag)

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { onSelect(tag) }
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(8.dp)
                            .background(tag.getColor())
                    )
                    Text(
                        text = tag.label,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                        color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onBackground,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                    if (isSelected)
                        Text(
                            text = "SELECTED",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                }
            }
        }
    }
}
