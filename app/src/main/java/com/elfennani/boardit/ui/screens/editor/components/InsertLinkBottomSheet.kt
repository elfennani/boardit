package com.elfennani.boardit.ui.screens.editor.components

import android.util.Patterns
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.ui.components.BottomSheet
import com.elfennani.boardit.ui.components.InputField

@Composable
fun InsertLinkBottomSheet(onClose: () -> Unit, onConfirm: (String) -> Unit) {
    var labelValue by remember { mutableStateOf(TextFieldValue("")) }
    var error: String? by remember { mutableStateOf(null) }

    BottomSheet(
        onDismissRequest = onClose,
        title = "Insert Link",
        onConfirm = {
            error = when{
                labelValue.text == "" -> "Link can't be empty"
                !Patterns.WEB_URL.matcher(labelValue.text).matches() -> "Invalid URL"

                else -> {
                    onConfirm(labelValue.text)
                    it()

                    null
                }
            }
        }
    ) {
        InputField(
            label = "Link",
            value = labelValue,
            onValueChanged = { labelValue = it },
            error = error,
        )
    }
}