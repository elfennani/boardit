package com.elfennani.boardit.data.models

import com.elfennani.boardit.data.local.models.SerializableBoard
import com.elfennani.boardit.data.local.models.SerializableCategory
import com.elfennani.boardit.data.local.models.SerializableTag

data class Merge(
    val boards: MergeChanges<SerializableBoard>,
    val tags: MergeChanges<SerializableTag>,
    val categories: MergeChanges<SerializableCategory>
)
