package com.elfennani.boardit.data.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideCategoryDao(database: AppDatabase) =
        database.categoryDao()

    @Singleton
    @Provides
    fun provideTagDao(database: AppDatabase) =
        database.tagDao()

    @Singleton
    @Provides
    fun provideSettingDao(database: AppDatabase) =
        database.settingDao()

    @Singleton
    @Provides
    fun provideBoardDao(database: AppDatabase) =
        database.boardDao()

    @Singleton
    @Provides
    fun provideBoardTagsDao(database: AppDatabase) =
        database.boardTagsDao()

    @Singleton
    @Provides
    fun provideAttachmentDao(database: AppDatabase) =
        database.attachmentDao()
}