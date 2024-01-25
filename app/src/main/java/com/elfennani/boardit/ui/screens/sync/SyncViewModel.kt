package com.elfennani.boardit.ui.screens.sync

import androidx.lifecycle.ViewModel
import com.elfennani.boardit.data.repository.SyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncRepository: SyncRepository
) : ViewModel() {

    fun sync(){
        Thread{
            syncRepository.sync()
        }.start()
    }

}