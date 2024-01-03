package com.elfennani.boardit.ui.screens.manage

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageViewModel @Inject constructor(
//    private val loadCategoriesUseCase: LoadCategoriesUseCase,
//    private val loadTagsUseCase: LoadTagsUseCase
) : ViewModel() {

    private val _manageScreenState = mutableStateOf(ManageScreenState(true, null, true, null))
    val manageScreenState: State<ManageScreenState> = _manageScreenState

    init {
        loadCategories()
        loadTags()
    }

    private fun loadTags() {
//        viewModelScope.launch {
//            val tags = loadTagsUseCase()
//            _manageScreenState.value = _manageScreenState.value.copy(isLoadingTags = false, tags = tags)
//        }
    }

    private fun loadCategories() {
//        viewModelScope.launch {
//            val categories = loadCategoriesUseCase()
//            _manageScreenState.value =
//                _manageScreenState.value.copy(isLoadingCategories = false, categories = categories)
//        }
    }

}