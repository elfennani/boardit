package com.elfennani.boardit.data

import com.elfennani.boardit.data.repository.CategoryRepository
import com.elfennani.boardit.data.repository.CategoryRepositoryImpl
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
}