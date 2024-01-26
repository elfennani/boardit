package com.elfennani.boardit.data.models

data class MergeChanges<T>(
    val added: List<T>,
    val deleted: List<T>,
    val updates: List<T>
)
