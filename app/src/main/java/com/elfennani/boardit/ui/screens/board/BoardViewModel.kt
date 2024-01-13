package com.elfennani.boardit.ui.screens.board

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.elfennani.boardit.data.models.Attachment
import com.elfennani.boardit.data.models.Board
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.CachedAttachmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.BucketApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoardViewModel @Inject constructor(
    private val boardRepository: BoardRepository,
    private val savedStateHandle: SavedStateHandle,
    private val bucketApi: BucketApi,
    private val cachedAttachmentRepository: CachedAttachmentRepository
) : ViewModel() {
    fun requestAttachment(attachment: Attachment) {
        viewModelScope.launch {
            cachedAttachmentRepository.download(attachment)
        }
    }

    private val boardId = checkNotNull(savedStateHandle.get<String>("id")?.toInt())

    val state =
        combine(
            boardRepository.boards.map { it.find { board -> board.id == boardId } },
            cachedAttachmentRepository.currentlyDownloading,
            cachedAttachmentRepository.cachedAttachments
        ) { board, currentlyDownloading, cachedAttachments ->
            BoardScreenState(board, currentlyDownloading, cachedAttachments)
        }.stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BoardScreenState()
        )
}