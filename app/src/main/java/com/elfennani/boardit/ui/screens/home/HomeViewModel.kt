package com.elfennani.boardit.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository,
    private val boardRepository: BoardRepository
) : ViewModel() {
    private val _categories = categoryRepository.categories
    private val _tags = tagRepository.tags
    private val _boards = boardRepository.boards

    val homeScreenState: StateFlow<HomeScreenState> =
        combine(
            _categories,
            _tags,
            _boards,
            categoryRepository.selectedCategory
        ) { categories, tags, boards, selectedCategory ->
            HomeScreenState(
                isLoadingCategories = false,
                categories = categories,
                isLoadingTags = false,
                tags = tags,
                currentCategory = if (selectedCategory == null) SelectedCategory.All else SelectedCategory.Id(
                    selectedCategory
                ),
                boards = boards.filter { selectedCategory == null || (selectedCategory == it.category.id) }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenState(
                isLoadingCategories = true,
                categories = runBlocking { categoryRepository.categories.firstOrNull() },
                isLoadingTags = true,
                tags = null,
                currentCategory = runBlocking {
                    val selectedCategory = categoryRepository.selectedCategory.firstOrNull()
                    if (selectedCategory != null) SelectedCategory.Id(selectedCategory)
                    else SelectedCategory.All
                },
                boards = null
            )
        )


    fun selectCategory(category: Category?) {
        viewModelScope.launch {
            categoryRepository.setSelectedCategory(category)
        }
    }

    init {
        viewModelScope.launch {
            categoryRepository.synchronize().runCatching { }
            tagRepository.synchronize().runCatching { }
            boardRepository.synchronize().runCatching { }
        }
    }
}