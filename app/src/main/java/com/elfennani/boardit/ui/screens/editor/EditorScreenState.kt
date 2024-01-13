package com.elfennani.boardit.ui.screens.editor

import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.EditorAttachment
import com.elfennani.boardit.data.models.Tag

enum class ModalState {
    CLOSED, SELECT_CATEGORY, SELECT_TAGS, INSERT_LINK
}

data class EditorScreenState(
    val titleTextFieldValue: TextFieldValue = TextFieldValue(),
    val bodyTextFieldValue: TextFieldValue = TextFieldValue(),
    val selectedCategory: Category? = null,
    val selectedTags: Set<Tag> = emptySet(),
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val attachments: List<EditorAttachment> = emptyList(),
    val modalState: ModalState = ModalState.CLOSED,
    val isDone: Boolean = false,
    val isSaving: Boolean = false,
    val isNew: Boolean = true,
    val isDeleting: Boolean = false,
    val isDoneDeleting: Boolean = false,
)
