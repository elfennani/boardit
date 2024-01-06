package com.elfennani.boardit.ui.screens.home

import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag

sealed class SelectedCategory(){
    data object All: SelectedCategory()
    data object Loading: SelectedCategory()
    data class Id(val id: Int): SelectedCategory()
}

data class HomeScreenState(
    val isLoadingCategories: Boolean,
    val categories: List<Category>?,
    val isLoadingTags: Boolean,
    val tags: List<Tag>?,
    val currentCategory: SelectedCategory,
    val boards: List<Board>?
)
