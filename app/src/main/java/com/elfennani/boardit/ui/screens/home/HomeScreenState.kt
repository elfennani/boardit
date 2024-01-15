package com.elfennani.boardit.ui.screens.home

import androidx.compose.ui.text.input.TextFieldValue
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag

sealed class SelectedCategory(){
    data object All: SelectedCategory()
    data object Loading: SelectedCategory()
    data class Id(val id: String): SelectedCategory()
}

data class HomeScreenState(
    val categories: List<Category> = emptyList(),
    val tags: List<Tag> = emptyList(),
    val currentCategory: SelectedCategory = SelectedCategory.Loading,
    val boards: List<Board> = emptyList(),
    val isFilteringTags: Boolean = false,
    val isSearching: Boolean = false,
    val filteredTags: Set<Tag> = emptySet(),
    val searchValue: TextFieldValue = TextFieldValue()
)
