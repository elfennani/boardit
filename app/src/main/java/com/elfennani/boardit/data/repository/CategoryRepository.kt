package com.elfennani.boardit.data.repository

import com.elfennani.boardit.data.local.dao.CategoryDao
import com.elfennani.boardit.data.local.dao.SettingDao
import com.elfennani.boardit.data.local.entities.CategoryEntity
import com.elfennani.boardit.data.local.entities.SettingEntity
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.remote.models.NetworkCategory
import com.elfennani.boardit.data.remote.models.asEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
private data class CategoryInsert(
    val label: String,
    @SerialName("user_id") val userId: String
)

interface CategoryRepository {
    val categories: Flow<List<Category>>
    val selectedCategory: Flow<Int?>

    val tableName: String
        get() = "category"

    suspend fun setSelectedCategory(category: Category?)

    suspend fun synchronize()
    suspend fun add(label: String)
    suspend fun edit(category: Category)
    suspend fun delete(category: Category)
}

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val supabaseClient: SupabaseClient,
    private val settingDao: SettingDao,
) : CategoryRepository {
    private val selectedCategoryKey = "selectedCategory"

    override val categories: Flow<List<Category>>
        get() = categoryDao.getAll().map { it.map(CategoryEntity::asExternalModel) }

    override val selectedCategory: Flow<Int?>
        get() = settingDao
            .getSettingByKey(selectedCategoryKey)
            .map { it?.value?.toInt() }

    override suspend fun setSelectedCategory(category: Category?) {
        if(category == null){
            settingDao.deleteSettingByKey(selectedCategoryKey)
            return;
        }

        settingDao.upsertSetting(
            SettingEntity(
                key = selectedCategoryKey,
                value = category.id.toString()
            )
        )
    }

    override suspend fun synchronize() {
        val currentUser = supabaseClient.auth.currentUserOrNull() ?: return
        val networkCategories: List<NetworkCategory> = supabaseClient.from(tableName)
            .select {
                filter {
                    NetworkCategory::userId eq currentUser.id
                }
            }.decodeList()

        categoryDao.deleteNotExist(networkCategories.map { it.id })
        categoryDao.upsertBatchCategory(
            networkCategories
                .map(NetworkCategory::asEntity)
        )
    }

    override suspend fun add(label: String) {
        val currentUser = checkNotNull(supabaseClient.auth.currentUserOrNull())
        supabaseClient
            .from(tableName)
            .insert(CategoryInsert(label, currentUser.id))
        synchronize()
    }

    override suspend fun edit(category: Category) {
        val currentUser = checkNotNull(supabaseClient.auth.currentUserOrNull())
        supabaseClient
            .from(tableName)
            .update({
                NetworkCategory::label setTo category.label
            }) {
                filter {
                    NetworkCategory::id eq category.id
                    NetworkCategory::userId eq currentUser.id
                }
            }

        synchronize()
    }

    override suspend fun delete(category: Category) {
        val currentUser = checkNotNull(supabaseClient.auth.currentUserOrNull())
        supabaseClient
            .from(tableName)
            .delete {
                filter {
                    NetworkCategory::id eq category.id
                    NetworkCategory::userId eq currentUser.id
                }
            }

        synchronize()
    }
}