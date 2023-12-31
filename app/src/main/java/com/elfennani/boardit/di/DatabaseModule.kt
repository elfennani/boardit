package com.elfennani.boardit.di

import android.content.Context
import com.elfennani.boardit.data.local.AppDatabase
import com.elfennani.boardit.domain.dao.FolderDao
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
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    fun provideFolderDao(database: AppDatabase): FolderDao = database.folderDao()

}