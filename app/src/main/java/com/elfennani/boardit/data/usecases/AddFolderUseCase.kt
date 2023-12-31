package com.elfennani.boardit.data.usecases

import com.elfennani.boardit.domain.dao.FolderDao
import com.elfennani.boardit.domain.entities.Folder
import kotlinx.coroutines.flow.Flow

class AddFolderUseCase (private val folderDao: FolderDao) {
    suspend operator fun invoke(folder: Folder) {
        return folderDao.upsertFolder(folder)
    }
}