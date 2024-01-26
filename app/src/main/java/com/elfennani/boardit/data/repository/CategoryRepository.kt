package com.elfennani.boardit.data.repository

import com.elfennani.boardit.data.local.models.SerializableCategory
import com.elfennani.boardit.data.local.models.asExternalModel
import com.elfennani.boardit.data.models.Category
import com.elfennani.boardit.data.models.serialize
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import javax.inject.Inject

interface CategoryRepository {
    val categories: Flow<List<Category>>
    val selectedCategory: Flow<Category?>

    fun setSelectedCategory(category: Category?)
    fun synchronize()
    fun add(label: String)
    fun edit(category: Category)
    fun delete(category: Category)
    fun getCategories(): List<SerializableCategory>
    val deletedCategories: List<String>
}

class CategoryRepositoryImpl @Inject constructor(
    private val mmkv: MMKV
) : CategoryRepository {

    private val _selected = MutableStateFlow<Category?>(getSelectedCategory())
    private val _categories = MutableStateFlow(getCategories())

    override fun getCategories(): List<SerializableCategory> = mmkv
        .allKeys()
        ?.toList()
        ?.filter { it.startsWith("category:") }
        ?.map {
            Json.decodeFromString<SerializableCategory>(mmkv.decodeString(it)!!)
        } ?: emptyList()

    private fun getSelectedCategory(): Category? {
        val selected = mmkv
            .decodeString("selected:category", null) ?: return null
        val category = mmkv.decodeString("category:$selected") ?: return null
        return Json.decodeFromString<SerializableCategory>(category).asExternalModel()
    }


    override val selectedCategory: Flow<Category?>
        get() = _selected
    override val categories: Flow<List<Category>>
        get() = _categories.map { it.map { category -> category.asExternalModel() } }

    override fun synchronize() {
        _categories.value = getCategories()
        _selected.value = getSelectedCategory()
    }

    override fun add(label: String) {
        val category = Category(label = label, modified = LocalDateTime.now())
        mmkv.encode("category:${category.id}", Json.encodeToString(category.serialize()))
        synchronize()
    }

    override fun edit(category: Category) {
        val modifiedCategory = category.copy(modified = LocalDateTime.now())
        mmkv.encode("category:${category.id}", Json.encodeToString(modifiedCategory.serialize()))
        synchronize()
    }

    override fun delete(category: Category) {
        mmkv.remove("category:${category.id}")
        mmkv.encode("deleted-category:${category.id}", category.id)
        synchronize()
    }

    override val deletedCategories: List<String>
        get() = mmkv
            .allKeys()
            ?.toList()
            ?.filter { it.startsWith("deleted-category:") }
            ?.map { mmkv.decodeString(it)!! }
            ?: emptyList()


    override fun setSelectedCategory(category: Category?) {
        if (category == null)
            mmkv.remove("selected:category")
        else
            mmkv.encode("selected:category", category.id)

        synchronize()
    }
}