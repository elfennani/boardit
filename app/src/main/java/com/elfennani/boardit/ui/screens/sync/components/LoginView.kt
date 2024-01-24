package com.elfennani.boardit.ui.screens.sync.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.elfennani.boardit.R

@Composable
fun ColumnScope.LoginView(
    onLogin: () -> Unit
){
    Box(Modifier.padding(horizontal = 16.dp)) {
        Image(
            painter = painterResource(id = R.drawable.uploading),
            contentDescription = null,
            modifier = Modifier
                .widthIn(max = 296.dp)
                .fillMaxWidth()
                .aspectRatio(296f / 234)
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    val title = buildAnnotatedString {
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
            append("Sync ")
        }
        append("with Google Drive")
    }
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = "Sync your mood boards with Google Drive for easy access anytime, anywhere. Your inspirations, always within reach.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
        textAlign = TextAlign.Center,
        lineHeight = 21.sp,
        modifier = Modifier.widthIn(max = 296.dp)
    )
    Spacer(modifier = Modifier.weight(1f))
    Button(
        onClick = onLogin,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
    ) {
        Icon(
            Icons.Default.Login,
            null,
            Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Login with Google",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }

}