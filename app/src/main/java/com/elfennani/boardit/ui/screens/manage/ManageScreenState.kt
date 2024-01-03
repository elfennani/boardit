package com.elfennani.boardit.ui.screens.manage

import com.elfennani.boardit.data.remote.models.CategoryDto
import com.elfennani.boardit.data.remote.models.TagDto

data class ManageScreenState(
    val isLoadingCategories: Boolean,
    val categories: List<CategoryDto>?,
    val isLoadingTags: Boolean,
    val tagDtos: List<TagDto>?
)
