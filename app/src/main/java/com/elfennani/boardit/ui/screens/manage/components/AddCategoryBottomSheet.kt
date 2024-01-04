package com.elfennani.boardit.ui.screens.manage.components

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
fun AddCategoryBottomSheet(onClose: () -> Unit, onConfirm: (label: String) -> Unit) {
    var labelValue by remember { mutableStateOf(TextFieldValue("")) }
    var error: String? by remember { mutableStateOf(null) }

    BottomSheet(
        onDismissRequest = onClose,
        title = "Add Category",
        onConfirm = {
            if (labelValue.text != "") {
                error = null
                onConfirm(labelValue.text)
                it()
            } else {
                error = "Label can't be empty"
            }
        }
    ) {
        InputField(
            label = "Label",
            value = labelValue,
            onValueChanged = { labelValue = it },
            error = error,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
        )
    }
}