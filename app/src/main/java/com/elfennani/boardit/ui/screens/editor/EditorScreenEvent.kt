package com.elfennani.boardit.ui.screens.editor

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.EditorAttachment
import com.elfennani.boardit.data.models.Tag

sealed class EditorScreenEvent {
    data class ModifyTitle(val textFieldValue: TextFieldValue) : EditorScreenEvent()
    data class ModifyBody(val textFieldValue: TextFieldValue) : EditorScreenEvent()
    data class OpenModal(val modalState: ModalState) : EditorScreenEvent()
    data class SelectCategory(val category: Category) : EditorScreenEvent()
    data class SelectTag(val tag: Tag) : EditorScreenEvent()
    data class PickImages(val uris: List<Uri>, val context: Context) : EditorScreenEvent()
    data class PickPdfs(val uris: List<Uri>, val context: Context) : EditorScreenEvent()
    data class PickLink(val url: String): EditorScreenEvent()
    data object CloseModal : EditorScreenEvent()
    data object Save : EditorScreenEvent()
    data class DeleteAttachment(val editorAttachment: EditorAttachment) : EditorScreenEvent()
    data object DeleteBoard : EditorScreenEvent()
}