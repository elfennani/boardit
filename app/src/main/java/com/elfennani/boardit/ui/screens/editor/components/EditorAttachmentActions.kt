package com.elfennani.boardit.ui.screens.editor.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun EditorAttachmentActions(
    onImage: () -> Unit,
    onLink: () -> Unit,
    onPdf: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(onClick = onLink) {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                tint = Color(0xFF94A3B8)
            )
        }
        IconButton(onClick = onPdf) {
            Icon(
                imageVector = Icons.Outlined.PictureAsPdf,
                contentDescription = null,
                tint = Color(0xFF94A3B8)
            )
        }
        IconButton(onClick = onImage) {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                tint = Color(0xFF94A3B8)
            )
        }
    }

}