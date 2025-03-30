package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
data class Player(
    @ColumnInfo(name = "p_id") @PrimaryKey(autoGenerate = true) val pId: Int = 0,
    @ColumnInfo(name = "p_name") val pName: String,
    @ColumnInfo(name = "p_archive") val pArchive: Int = 0
)
