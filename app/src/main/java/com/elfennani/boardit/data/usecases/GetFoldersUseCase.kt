package com.elfennani.boardit.data.usecases

import com.elfennani.boardit.domain.dao.FolderDao
import com.elfennani.boardit.domain.entities.Folder
import kotlinx.coroutines.flow.Flow

class GetFoldersUseCase(private val folderDao: FolderDao) {
    operator fun invoke():Flow<List<Folder>>{
        return folderDao.getAll()
    }
}