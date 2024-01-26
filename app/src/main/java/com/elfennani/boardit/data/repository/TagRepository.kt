package com.elfennani.boardit.data.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.elfennani.boardit.data.local.models.SerializableTag
import com.elfennani.boardit.data.local.models.asExternalModel
import com.elfennani.boardit.data.models.MergeChanges
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.models.serialize
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

interface TagRepository {
    val tags: Flow<List<Tag>>
    val deletedTags: List<String>

    fun synchronize()
    fun add(label: String, color: Color, id: String = UUID.randomUUID().toString())
    fun delete(tag: Tag)
    fun delete(id: String)
    fun getTags(): List<SerializableTag>
    fun merge(changes: MergeChanges<SerializableTag>)
    fun edit(tag: Tag, modify: Boolean=true)
}

class TagRepositoryImpl @Inject constructor(
    private val mmkv: MMKV
) : TagRepository {

    private val _tags = MutableStateFlow(getTags())

    override fun getTags(): List<SerializableTag> {
        val tagKeys = mmkv
            .allKeys()
            ?.toList()
            ?.filter { it.startsWith("tag:") }
            ?: return emptyList()

        return tagKeys.mapNotNull {
            val tag = mmkv.decodeString(it) ?: return@mapNotNull null
            Json.decodeFromString(tag)
        }
    }

    override val tags: Flow<List<Tag>>
        get() = _tags.map { it.map { tag -> tag.asExternalModel() } }

    override fun synchronize() {
        _tags.value = getTags()
        Log.d("TAGS", getTags().toString())
    }

    override fun add(label: String, color: Color, id: String) {
        val tag = Tag(
            id = id,
            label = label,
            color = color,
            modified = LocalDateTime.now()
        )
        edit(tag)
    }

    private fun add(tag: SerializableTag, modify: Boolean = true) {
        edit(tag.asExternalModel(), false)
    }

    override fun edit(tag: Tag, modify: Boolean) {
        val modifiedTag = if(modify) tag.copy(modified = LocalDateTime.now()) else tag
        mmkv.encode("tag:${tag.id}", Json.encodeToString(modifiedTag.serialize()))
        synchronize()
    }

    override fun delete(tag: Tag) {
        delete(tag.id)
    }

    override fun delete(id: String) {
        mmkv.remove("tag:$id")
        mmkv.encode("deleted-tag:$id", id)
        synchronize()
    }

    override fun merge(changes: MergeChanges<SerializableTag>) {
        changes.added.forEach {
            add(it, modify = false)
        }
        changes.updates.forEach {
            edit(it.asExternalModel(), false)
        }
        changes.deleted.forEach {
            delete(it.id)
        }
    }

    override val deletedTags: List<String>
        get() = mmkv
            .allKeys()
            ?.toList()
            ?.filter { it.startsWith("deleted-tag:") }
            ?.map { mmkv.decodeString(it)!! }
            ?: emptyList()
}