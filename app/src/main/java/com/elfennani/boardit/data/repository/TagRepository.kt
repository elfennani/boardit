package com.elfennani.boardit.data.repository

import android.util.Log
import androidx.compose.ui.graphics.Color
import com.elfennani.boardit.data.local.models.SerializableTag
import com.elfennani.boardit.data.local.models.asExternalModel
import com.elfennani.boardit.data.models.Tag
import com.elfennani.boardit.data.models.serialize
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

interface TagRepository {
    val tags: Flow<List<Tag>>

    fun synchronize()
    fun add(label: String, color: Color)
    fun edit(tag: Tag)
    fun delete(tag: Tag)
    fun getTags(): List<SerializableTag>
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

    override fun add(label: String, color: Color) {
        val tag = Tag(label = label, color = color)
        edit(tag)
    }

    override fun edit(tag: Tag) {
        mmkv.encode("tag:${tag.id}", Json.encodeToString(tag.serialize()))
        synchronize()
    }

    override fun delete(tag: Tag) {
        mmkv.remove("tag:${tag.id}")
        synchronize()
    }
}