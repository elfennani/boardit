package com.elfennani.boardit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elfennani.boardit.domain.dao.FolderDao
import com.elfennani.boardit.domain.entities.Folder

@Database(entities = [Folder::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "boardit"
                ).build()
            }
        }
    }

}