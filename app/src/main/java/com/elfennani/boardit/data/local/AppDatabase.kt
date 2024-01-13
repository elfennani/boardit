package com.elfennani.boardit.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.elfennani.boardit.data.local.dao.AttachmentDao
import com.elfennani.boardit.data.local.dao.BoardDao
import com.elfennani.boardit.data.local.dao.BoardTagsDao
import com.elfennani.boardit.data.local.dao.CachedAttachmentDao
import com.elfennani.boardit.data.local.dao.CategoryDao
import com.elfennani.boardit.data.local.dao.SettingDao
import com.elfennani.boardit.data.local.dao.TagDao
import com.elfennani.boardit.data.local.entities.AttachmentEntity
import com.elfennani.boardit.data.local.entities.BoardEntity
import com.elfennani.boardit.data.local.entities.BoardTagsEntity
import com.elfennani.boardit.data.local.entities.CachedAttachmentEntity
import com.elfennani.boardit.data.local.entities.CategoryEntity
import com.elfennani.boardit.data.local.entities.SettingEntity
import com.elfennani.boardit.data.local.entities.TagEntity

@Database(
    entities = [
        CategoryEntity::class,
        TagEntity::class,
        SettingEntity::class,
        BoardEntity::class,
        BoardTagsEntity::class,
        AttachmentEntity::class,
        CachedAttachmentEntity::class
    ], version = 1,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun tagDao(): TagDao
    abstract fun settingDao(): SettingDao
    abstract fun boardDao(): BoardDao
    abstract fun boardTagsDao(): BoardTagsDao
    abstract fun attachmentDao(): AttachmentDao
    abstract fun cachedAttachmentDao(): CachedAttachmentDao

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