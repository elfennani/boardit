package com.elfennani.boardit.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.elfennani.boardit.data.local.dao.BoardTagsDao
import com.elfennani.boardit.data.local.dao.TagDao
import com.elfennani.boardit.data.local.entities.TagEntity
import com.elfennani.boardit.data.local.entities.asExternalModel
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.remote.models.NetworkBoardTags
import com.elfennani.boardit.data.remote.models.NetworkCategory
import com.elfennani.boardit.data.remote.models.NetworkTag
import com.elfennani.boardit.data.remote.models.asEntity
import com.elfennani.boardit.data.remote.models.combineIds
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

@Serializable
private data class TagInsert(
    val label: String,
    val color: String,
    @SerialName("user_id") val userId: String
)


interface TagRepository {
    val tags: Flow<List<Tag>>
    val tableName: String
        get() = "tag"

    suspend fun synchronize()
    suspend fun add(label: String, color: Color)
    suspend fun edit(tag: Tag)
    suspend fun delete(tag: Tag)
}

class TagRepositoryImpl @Inject constructor(
    private val tagDao: TagDao,
    private val boardTagsDao: BoardTagsDao,
    private val supabaseClient: SupabaseClient
) : TagRepository {
    override val tags: Flow<List<Tag>>
        get() = tagDao.getAll().map { it.map(TagEntity::asExternalModel) }

    override suspend fun synchronize() {
        val currentUser = supabaseClient.auth.currentUserOrNull() ?: return
        val networkTags: List<NetworkTag> = supabaseClient.from(tableName)
            .select {
                filter {
                    NetworkTag::userId eq currentUser.id
                }
            }.decodeList()

        val boardTagsNetwork: List<NetworkBoardTags> = supabaseClient
            .from("board_tags")
            .select {
                filter {
                    NetworkTag::userId eq currentUser.id
                }
            }.decodeList()

        tagDao.deleteNotExisting(networkTags.map { it.id })
        tagDao.upsertBatchTag(
            networkTags
                .map(NetworkTag::asEntity)
        )
        boardTagsDao.deleteNotExist(boardTagsNetwork.map(NetworkBoardTags::combineIds))
        boardTagsDao.upsertBatchBoardTags(boardTagsNetwork.map(NetworkBoardTags::asEntity))
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun Color.toHex() = this.toArgb()
        .toHexString(HexFormat.UpperCase)
        .replaceFirst("FF", "#")

    override suspend fun add(label: String, color: Color) {
        val currentUser = checkNotNull(supabaseClient.auth.currentUserOrNull())
        supabaseClient
            .from(tableName)
            .insert(
                TagInsert(
                    label,
                    color.toHex(),
                    currentUser.id
                )
            )
        synchronize()
    }

    override suspend fun edit(tag: Tag) {
        val currentUser = checkNotNull(supabaseClient.auth.currentUserOrNull())
        supabaseClient
            .from(tableName)
            .update({
                NetworkTag::label setTo tag.label
                NetworkTag::color setTo tag.color.toHex()
            }) {
                filter {
                    NetworkTag::id eq tag.id
                    NetworkTag::userId eq currentUser.id
                }
            }

        synchronize()
    }

    override suspend fun delete(tag: Tag) {
        val currentUser = checkNotNull(supabaseClient.auth.currentUserOrNull())
        supabaseClient
            .from(tableName)
            .delete {
                filter {
                    NetworkTag::id eq tag.id
                    NetworkTag::userId eq currentUser.id
                }
            }

        synchronize()
    }
}