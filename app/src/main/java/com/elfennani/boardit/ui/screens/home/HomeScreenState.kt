package com.elfennani.boardit.ui.screens.home

import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag

data class HomeScreenState(
    val isLoadingCategories: Boolean,
    val categories: List<Category>?,
    val isLoadingTags: Boolean,
    val tags: List<Tag>?
)
