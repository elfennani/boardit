package com.elfennani.boardit.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.elfennani.boardit.data.local.entities.SettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {

    @Query("SELECT * FROM settings WHERE `key` = :key")
    fun getSettingByKey(key: String) : Flow<SettingEntity?>

    @Upsert
    suspend fun upsertSetting(settingsEntity: SettingEntity)
    @Delete
    suspend fun deleteSetting(settingEntity: SettingEntity)
    @Query("DELETE FROM settings WHERE `key` = :key")
    suspend fun deleteSettingByKey(key:String)
}