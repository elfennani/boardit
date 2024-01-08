package com.elfennani.boardit.ui.screens.editor

import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag

sealed class EditorScreenEvent {
    data class ModifyTitle(val textFieldValue: TextFieldValue) : EditorScreenEvent()
    data class ModifyBody(val textFieldValue: TextFieldValue) : EditorScreenEvent()
    data class OpenModal(val modalState: ModalState) : EditorScreenEvent()
    data class SelectCategory(val category: Category) : EditorScreenEvent()
    data class SelectTag(val tag: Tag) : EditorScreenEvent()
    data object CloseModal : EditorScreenEvent()
    data object Save : EditorScreenEvent()
}