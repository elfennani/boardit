package com.elfennani.boardit.ui.screens.manage

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
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

    val manageScreenState: StateFlow<ManageScreenState> =
        combine(categoryRepository.categories, tagRepository.tags) { categories, tags ->
            ManageScreenState(
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

    fun addCategory(label: String) {
        viewModelScope.launch {
            try {
                categoryRepository.add(label)
            } catch (_: Exception) {
            }
        }
    }

    fun editCategory(category: Category){
        viewModelScope.launch {
            try {
                categoryRepository.edit(category)
            }catch (_:Exception){}
        }
    }

    fun deleteCategory(category: Category){
        viewModelScope.launch {
            try {
                categoryRepository.delete(category)
            }catch (_:Exception){}
        }
    }

    fun addTag(label: String, color: Color){
        viewModelScope.launch {
            try {
                tagRepository.add(label, color)
            }catch (_:Exception){}
        }
    }

    fun editTag(tag:Tag){
        viewModelScope.launch {
            try {
                tagRepository.edit(tag)
            }catch (_:Exception){}
        }
    }

    fun deleteTag(tag:Tag){
        viewModelScope.launch {
            try {
                tagRepository.delete(tag)
            }catch (_:Exception){}
        }
    }
}