package com.elfennani.boardit.ui.screens.manage.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AddButton(onAdd: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(400.dp))
            .clickable { onAdd() }
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(400.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = null,
            Modifier.size(14.dp)
        )
        Text(text = "Add", style = MaterialTheme.typography.labelSmall)
    }
}
