package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lohni.darts.room.enums.SettingType

@Entity(tableName = "settings")
data class Setting(
    @ColumnInfo(name = "s_id") @PrimaryKey val sId: SettingType,
    @ColumnInfo(name = "s_value") var sValue: String
)
