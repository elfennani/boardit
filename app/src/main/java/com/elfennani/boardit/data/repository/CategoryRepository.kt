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
import javax.inject.Inject

interface CategoryRepository {
    val categories: Flow<List<Category>>
    val selectedCategory: Flow<Category?>

    fun setSelectedCategory(category: Category?)
    fun synchronize()
    fun add(label: String)
    fun edit(category: Category)
    fun delete(category: Category)
}

class CategoryRepositoryImpl @Inject constructor(
    private val mmkv: MMKV
) : CategoryRepository {

    private val _selected = MutableStateFlow<Category?>(getSelectedCategory())
    private val _categories = MutableStateFlow(getCategories())

    private fun getCategories(): List<SerializableCategory> = mmkv
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
        val category = Category(label = label)
        mmkv.encode("category:${category.id}", Json.encodeToString(category.serialize()))
        synchronize()
    }

    override fun edit(category: Category) {
        mmkv.encode("category:${category.id}", Json.encodeToString(category.serialize()))
        synchronize()
    }

    override fun delete(category: Category) {
        mmkv.remove("category:${category.id}")
        synchronize()
    }

    override fun setSelectedCategory(category: Category?) {
        if (category == null)
            mmkv.remove("selected:category")
        else
            mmkv.encode("selected:category", category.id)

        synchronize()
    }
}