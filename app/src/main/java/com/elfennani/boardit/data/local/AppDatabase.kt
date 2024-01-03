package com.elfennani.boardit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.elfennani.boardit.data.local.dao.CategoryDao
import com.elfennani.boardit.data.local.entities.CategoryEntity

@Database(entities = [CategoryEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

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