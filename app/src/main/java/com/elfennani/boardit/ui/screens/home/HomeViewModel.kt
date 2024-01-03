package com.elfennani.boardit.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _categories = categoryRepository.categories

    //    private val _homeScreenState = MutableStateFlow(HomeScreenState(true, null, true, null))
    val homeScreenState: StateFlow<HomeScreenState> = _categories.map {
        HomeScreenState(
            isLoadingCategories = false,
            categories = it,
            isLoadingTags = true,
            tagDtos = null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeScreenState(
            isLoadingCategories = true,
            categories = null,
            isLoadingTags = true,
            tagDtos = null
        )
    )

    init {
        loadCategories()
        loadTags()
    }

    private fun loadTags() {
//        viewModelScope.launch {
//            val tags = loadTagsUseCase()
//            _homeScreenState.value = _homeScreenState.value.copy(isLoadingTags = false, tags = tags)
//        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.synchronize()
        }
    }
}