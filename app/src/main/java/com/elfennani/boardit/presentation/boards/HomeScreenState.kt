package com.elfennani.boardit.presentation.boards

import com.elfennani.boardit.domain.model.Category

data class HomeScreenState(
    val isLoadingCategories: Boolean,
    val categories: List<Category>?
)
