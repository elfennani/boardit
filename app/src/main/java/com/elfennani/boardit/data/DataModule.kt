package com.elfennani.boardit.data

import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.CategoryRepositoryImpl
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
}