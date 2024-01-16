package com.elfennani.boardit.ui.screens.home

import android.util.Log
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import java.time.ZoneOffset
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
    private val _state = MutableStateFlow(HomeScreenState())

    val homeScreenState: StateFlow<HomeScreenState> =
        combine(
            _boards,
            _categories,
            _selected,
            _tags,
            _state,
        ) { boards, categories, selected, tags, state ->
            val currentCategory = when (selected) {
                null -> SelectedCategory.All
                else -> SelectedCategory.Id(selected.id)
            }

            val filteredBoards = boards
                .sortedBy { it.date.toInstant(ZoneOffset.UTC).epochSecond }
                .filter { currentCategory !is SelectedCategory.Id || currentCategory.id == it.category?.id }
                .filter {
                    state.filteredTags.isEmpty() || it.tags.any { tag ->
                        state.filteredTags.contains(
                            tag
                        )
                    }
                }
                .filter {
                    state.searchValue.text.isEmpty() ||
                            it.title.lowercase().contains(state.searchValue.text.lowercase()) ||
                            (it.note ?: "").lowercase().contains(state.searchValue.text.lowercase())
                }

            state.copy(
                categories = categories,
                tags = tags,
                currentCategory = currentCategory,
                boards = filteredBoards,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeScreenState(boards = runBlocking {
                boardRepository.boards.first()
                    .sortedBy { it.date.toInstant(ZoneOffset.UTC).epochSecond }
            })
        )

    fun selectCategory(category: Category?) {
        categoryRepository.setSelectedCategory(category)
    }

    fun toggleSearchInput() {
        Log.d("SelectInput", _state.value.toString())
        if (_state.value.isSearching) {
            _state.value = _state.value.copy(isSearching = false, searchValue = TextFieldValue())
        } else {
            _state.value = _state.value.copy(isSearching = true)
        }
    }

    fun openFilterTagsModal() {
        _state.value = _state.value.copy(isFilteringTags = true)
    }

    fun closeFilterTagsModal() {
        _state.value = _state.value.copy(isFilteringTags = false)
    }

    fun filterTag(tag: Tag) {
        val filteredTags = _state.value.filteredTags
        val newFilter = if (filteredTags.contains(tag)) {
            filteredTags - tag
        } else {
            filteredTags + tag
        }

        _state.value = _state.value.copy(filteredTags = newFilter)
    }

    fun setSearchValue(textFieldValue: TextFieldValue) {
        _state.value = _state.value.copy(searchValue = textFieldValue)
    }
}