package com.elfennani.boardit.ui.screens.home

import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.remote.models.TagDto

data class HomeScreenState(
    val isLoadingCategories: Boolean,
    val categories: List<Category>?,
    val isLoadingTags: Boolean,
    val tagDtos: List<TagDto>?
)
