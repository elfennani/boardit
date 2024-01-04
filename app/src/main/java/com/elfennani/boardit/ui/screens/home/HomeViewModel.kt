package com.elfennani.boardit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    private val _categories = categoryRepository.categories
    private val _tags = tagRepository.tags

    val homeScreenState: StateFlow<HomeScreenState> =
        combine(_categories, _tags) { categories, tags ->
            HomeScreenState(
                isLoadingCategories = false,
                categories = categories,
                isLoadingTags = false,
                tags = tags
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenState(
                isLoadingCategories = true,
                categories = null,
                isLoadingTags = true,
                tags = null
            )
        )

    init {
        viewModelScope.launch {
            try {
                categoryRepository.synchronize()
            } catch (_: Exception) {
            }
            try {
                tagRepository.synchronize()
            } catch (_: Exception) {
            }
        }
    }
}