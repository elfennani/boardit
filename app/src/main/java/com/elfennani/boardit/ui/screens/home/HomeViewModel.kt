package com.elfennani.boardit.ui.screens.home

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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    boardRepository: BoardRepository,
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository
) : ViewModel() {
    private val _boards = boardRepository.boards
    private val _categories = categoryRepository.categories
    private val _selected = categoryRepository.selectedCategory
    private val _tags = tagRepository.tags

    val homeScreenState: StateFlow<HomeScreenState> =
        combine(_boards, _categories, _selected, _tags) { boards, categories, selected, tags ->
            val currentCategory = when(selected){
                null -> SelectedCategory.All
                else -> SelectedCategory.Id(selected.id)
            }
            val filteredBoards = when(currentCategory){
                is SelectedCategory.Id -> boards.filter { currentCategory.id == it.category?.id }
                else -> boards
            }

            HomeScreenState(
                isLoadingCategories = false,
                categories = categories,
                isLoadingTags = false,
                tags = tags,
                currentCategory = currentCategory,
                boards = filteredBoards
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenState(
                isLoadingCategories = true,
                categories = emptyList(),
                isLoadingTags = true,
                tags = emptyList(),
                currentCategory = SelectedCategory.All,
                boards = runBlocking { boardRepository.boards.first() }
            )
        )

    fun selectCategory(category: Category?){
        categoryRepository.setSelectedCategory(category)
    }
}