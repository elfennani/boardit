package com.elfennani.boardit.ui.screens.manage

import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag

data class ManageScreenState(
    val isLoadingCategories: Boolean,
    val categories: List<Category>?,
    val isLoadingTags: Boolean,
    val tags: List<Tag>?,
)
