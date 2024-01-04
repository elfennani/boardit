package com.elfennani.boardit.data.repository

import android.util.Log
import com.elfennani.boardit.data.local.dao.CategoryDao
import com.elfennani.boardit.data.local.dao.TagDao
import com.elfennani.boardit.data.local.entities.CategoryEntity
import com.elfennani.boardit.data.local.entities.TagEntity
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.remote.models.CategoryDto
import com.elfennani.boardit.data.remote.models.TagDto
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

interface TagRepository {
    val tags: Flow<List<Tag>>

    suspend fun synchronize()
}

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val supabaseClient: SupabaseClient
) : TagRepository{
    override val tags: Flow<List<Tag>>
        get() = tagDao.getAll().map { it.map(TagEntity::asExternalModel) }

    override suspend fun synchronize() {
        val currentUser = supabaseClient.auth.currentUserOrNull() ?: return
        val networkCategories: List<TagDto> = supabaseClient.from("tag")
            .select{
                filter {
                    TagDto::userId eq currentUser.id
                }
            }.decodeList()

        Log.d("TAGS", networkCategories.toString())

        networkCategories
            .map(TagDto::asEntity)
            .forEach {
                tagDao.upsertTag(it)
            }
    }
}