package com.elfennani.boardit.ui.screens.board.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ImageAttachment(
    url: String,
    onClick: () -> Unit,
    aspectRatio: Float = 16f / 9
) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            .clickable(onClick = onClick)
            .aspectRatio(aspectRatio),
        contentScale = ContentScale.Crop
    )
}
