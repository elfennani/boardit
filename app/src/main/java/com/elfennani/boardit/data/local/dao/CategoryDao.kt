package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
        @Query("SELECT * FROM category")
        fun getAll() : Flow<List<CategoryEntity>>
        @Query("SELECT * FROM category WHERE id = :id")
        fun getCategoryById(id: String) : Flow<CategoryEntity>
        @Upsert
        suspend fun upsertCategory(categoryEntity: CategoryEntity)
        @Upsert
        suspend fun upsertBatchCategory(categoryEntities: List<CategoryEntity>)
        @Query("DELETE FROM category WHERE category.id NOT IN (:ids)")
        suspend fun deleteNotExist(ids: List<Int>)
        @Delete
        suspend fun deleteCategory(categoryEntity: CategoryEntity)
}