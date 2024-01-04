package com.elfennani.boardit.ui.screens.manage.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.ui.components.BottomSheet
import com.elfennani.boardit.ui.components.InputField

@Composable
fun EditTagBottomSheet(
    tag: Tag,
    onClose: () -> Unit,
    onConfirm: (tag: Tag) -> Unit,
    onDelete: (tag: Tag) -> Unit
) {
    var labelValue by remember { mutableStateOf(TextFieldValue(tag.label)) }
    var error: String? by remember { mutableStateOf(null) }
    var pickedColor: Color by remember {
        mutableStateOf(tag.color)
    }

    BottomSheet(
        onDismissRequest = onClose,
        title = "Edit Category",
        onConfirm = {
            if (labelValue.text != "") {
                error = null
                onConfirm(tag.copy(label = labelValue.text, color = pickedColor))
                it()
            } else {
                error = "Label can't be empty"
            }
        },
        onDelete = {
            onDelete(tag)
            it()
        }
    ) {
        InputField(
            label = "Label",
            value = labelValue,
            onValueChanged = { labelValue = it },
            error = error,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Sentences)
        )
        ColorPicker(color = pickedColor, onPick = { pickedColor = it })
    }
}
