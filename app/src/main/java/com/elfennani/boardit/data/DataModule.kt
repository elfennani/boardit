package com.elfennani.boardit.data

import android.content.Context
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.BoardRepositoryImpl
import com.elfennani.boardit.data.repository.CachedAttachmentRepository
import com.elfennani.boardit.data.repository.CachedAttachmentRepositoryImpl
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.CategoryRepositoryImpl
import com.elfennani.boardit.data.repository.TagRepository
import com.elfennani.boardit.data.repository.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Singleton
    @Binds
    fun bindMyCategoryRepository(
        categoryRepository: CategoryRepositoryImpl
    ) : CategoryRepository

    @Singleton
    @Binds
    fun bindMyTagRepository(
        tagRepository: TagRepositoryImpl
    ) : TagRepository

    @Singleton
    @Binds
    fun bindBoardRepository(
        boardRepository: BoardRepositoryImpl
    ) : BoardRepository

    @Singleton
    @Binds
    fun bindCachedAttachmentRepository(
        cachedAttachmentRepository: CachedAttachmentRepositoryImpl
    ) : CachedAttachmentRepository
}