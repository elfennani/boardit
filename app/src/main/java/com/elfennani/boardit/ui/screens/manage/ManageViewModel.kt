package com.elfennani.boardit.ui.screens.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val tagRepository: TagRepository
) : ViewModel() {

    val manageScreenState: StateFlow<ManageScreenState> = combine(categoryRepository.categories, tagRepository.tags){
        categories, tags -> ManageScreenState(
            isLoadingCategories = false,
            isLoadingTags = false,
            tags = tags,
            categories = categories
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ManageScreenState(true, null, true, null)
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