package com.elfennani.boardit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.elfennani.boardit.ui.theme.BoarditTheme

@Composable
fun InputField(
    label: String,
    value: TextFieldValue,
    onValueChanged: (TextFieldValue) -> Unit,
    isPassword: Boolean = false,
    icon: ImageVector? = null,
    placeHolder: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    error: String? = null,
    footer: (@Composable () -> Unit)? = null
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = if(error.isNullOrEmpty()) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .fillMaxWidth()
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
            singleLine = true,
            keyboardOptions = keyboardOptions,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
        ) {
            Row(
                Modifier
                    .border(
                        1.dp,
                        if (error.isNullOrEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error,
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    if (value.text == "" && placeHolder != null) {
                        Text(
                            text = placeHolder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    it()
                }

                if (icon != null) Icon(
                    icon, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.tertiary
                )
            }
        }

        if (!error.isNullOrEmpty()) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        footer?.invoke()
    }
}

@Preview
@Composable
fun InputFieldPreview() {
    BoarditTheme {
        Column(
            Modifier
                .background(Color.White)
                .padding(16.dp)
                .width(250.dp)
        ) {
            InputField(label = "Label",
                placeHolder = "Placeholder...",
                icon = Icons.Rounded.Favorite,
                value = TextFieldValue("Lorem Lorem Lorem Lorem Lorem "),
                onValueChanged = {})
        }
    }
}