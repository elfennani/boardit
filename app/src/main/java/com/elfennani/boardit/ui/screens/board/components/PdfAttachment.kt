package com.elfennani.boardit.ui.screens.board.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import java.io.File

private fun Context.openPdf(uri: String) {
    val newIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    newIntent.setDataAndType(
        FileProvider.getUriForFile(
            this,
            this.packageName + ".provider",
            File(uri)
        ), "application/pdf"
    )
    newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    newIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    try {
        ContextCompat.startActivity(this, newIntent, bundleOf())
    } catch (e: Exception) {
        Log.e("INTENT", e.toString())
    }
}

@Composable
fun PdfAttachment(uri: String) {
    val context = LocalContext.current

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Rounded.PictureAsPdf, contentDescription = null)
        Button(onClick = {
            context.openPdf(uri)
        }) {
            Text(text = "Open")
        }
    }
}
