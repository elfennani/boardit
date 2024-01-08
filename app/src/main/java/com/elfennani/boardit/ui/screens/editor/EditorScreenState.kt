package com.elfennani.boardit.ui.screens.editor

import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag

enum class ModalState {
    CLOSED, SELECT_CATEGORY, SELECT_TAGS
}

data class EditorScreenState(
    val titleTextFieldValue: TextFieldValue = TextFieldValue(),
    val bodyTextFieldValue: TextFieldValue = TextFieldValue(),
    val selectedCategory: Category? = null,
    val selectedTags: Set<Tag> = emptySet(),
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val modalState: ModalState = ModalState.CLOSED,
    val isDone: Boolean = false,
    val isSaving: Boolean = false
)
