package com.elfennani.boardit.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String,
)
