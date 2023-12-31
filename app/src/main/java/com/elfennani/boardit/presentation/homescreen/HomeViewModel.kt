package com.elfennani.boardit.presentation.homescreen

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.usecases.AddFolderUseCase
import com.elfennani.boardit.data.usecases.GetFoldersUseCase
import com.elfennani.boardit.domain.entities.Folder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getFoldersUseCase: GetFoldersUseCase,
    private val addFolderUseCase: AddFolderUseCase
) :
    ViewModel() {
    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders: StateFlow<List<Folder>> = _folders

    private val _isAddingFolder = mutableStateOf(false)
    val isAddingFolder = _isAddingFolder



    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            getFoldersUseCase().collect {
                _folders.emit(it)
            }
        }
    }

    fun addFolder(title:String){
        viewModelScope.launch {
            _isAddingFolder.value = false
            addFolderUseCase(Folder(title = title, color = Color.White.toArgb()))
        }
    }
}