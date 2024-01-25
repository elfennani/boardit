package com.elfennani.boardit.data

import com.elfennani.boardit.data.models.LinkMetadata
import com.elfennani.boardit.data.repository.BoardRepository
import com.elfennani.boardit.data.repository.BoardRepositoryImpl
import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.CategoryRepositoryImpl
import com.elfennani.boardit.data.repository.LinkMetadataRepository
import com.elfennani.boardit.data.repository.LinkMetadataRepositoryImpl
import com.elfennani.boardit.data.repository.SyncRepository
import com.elfennani.boardit.data.repository.SyncRepositoryImpl
import com.elfennani.boardit.data.repository.TagRepository
import com.elfennani.boardit.data.repository.TagRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
    fun bindLinkMetadataRepository(
        linkMetadata: LinkMetadataRepositoryImpl
    ) : LinkMetadataRepository

    @Singleton
    @Binds
    fun bindSyncRepository(
        linkMetadata: SyncRepositoryImpl
    ) : SyncRepository
}