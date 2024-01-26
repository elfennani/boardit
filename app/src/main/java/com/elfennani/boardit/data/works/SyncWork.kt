package com.elfennani.boardit.data.works

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.SyncRepository
import com.elfennani.boardit.data.repository.TagRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class SyncWork @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncRepository: SyncRepository,
    private val boardRepository: BoardRepository,
    private val tagRepository: TagRepository,
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        return try {
            Thread {
                val data = syncRepository.data
                val onlineData = syncRepository.onlineData
                val newBoards = data.boards
                    .plus(onlineData.boards)
                    .sortedByDescending {
                        LocalDateTime.parse(it.modified, DateTimeFormatter.ISO_DATE_TIME)
                            .toInstant(ZoneOffset.UTC).epochSecond
                    }
                    .distinctBy { it.id }
                    .filter { data.deleted.boards.contains(it.id) }

                data.deleted.boards.forEach { boardRepository.deleteBoard(it) }
                data.deleted.tags.forEach { tagRepository.delete(it) }
                data.deleted.categories.forEach {  }

            }.start()

            Result.success()
        } catch (e: Exception) {
            Log.e("SYNCWORK", e.toString())
            Result.failure()
        }
    }
}