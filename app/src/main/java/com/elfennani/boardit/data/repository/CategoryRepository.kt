package com.elfennani.boardit.data.repository

import com.elfennani.boardit.data.local.dao.CategoryDao
import com.elfennani.boardit.data.local.entities.CategoryEntity
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.remote.models.CategoryDto
import com.elfennani.boardit.data.remote.models.asEntity
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CategoryRepository {
    val categories: Flow<List<Category>>

    suspend fun synchronize()
}

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val supabaseClient: SupabaseClient
) : CategoryRepository{
    override val categories: Flow<List<Category>>
        get() = categoryDao.getAll().map { it.map(CategoryEntity::asExternalModel) }

    override suspend fun synchronize() {
        val currentUser = supabaseClient.auth.currentUserOrNull() ?: return
        val networkCategories: List<CategoryDto> = supabaseClient.from("category")
            .select{
                filter {
                    CategoryDto::userId eq currentUser.id
                }
            }.decodeList()

        networkCategories
            .map(CategoryDto::asEntity)
            .forEach {
                categoryDao.upsertCategory(it)
            }
    }
}