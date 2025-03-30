package com.lohni.darts.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lohni.darts.room.entities.Setting
import com.lohni.darts.room.enums.SettingType
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE s_id = :type")
    fun getSetting(type: SettingType): Flow<Setting>

    @Query("SELECT s_value FROM settings WHERE s_id = :type")
    fun getSettingValue(type: SettingType): Flow<String>

    @Transaction
    @Update
    fun updateSetting(setting: Setting)
}