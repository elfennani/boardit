package com.elfennani.boardit.presentation.boards

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.usecases.LoadCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val supabaseClient: SupabaseClient,
    private val loadCategoriesUseCase: LoadCategoriesUseCase
) : ViewModel() {
    private val _homeScreenState = mutableStateOf(HomeScreenState(true, null))
    val homeScreenState : State<HomeScreenState> = _homeScreenState
    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val categories = loadCategoriesUseCase()
            _homeScreenState.value =
                _homeScreenState.value.copy(isLoadingCategories = false, categories = categories)
        }
    }
}