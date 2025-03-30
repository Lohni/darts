package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "leg",
    indices = [Index("l_set")],
    foreignKeys = [ForeignKey(
        entity = Set::class,
        parentColumns = ["s_id"],
        childColumns = ["l_set"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Player::class,
        parentColumns = ["p_id"],
        childColumns = ["l_winner"]
    )]
)
data class Leg(
    @ColumnInfo(name = "l_id") @PrimaryKey(autoGenerate = true) val lId: Int = 0,
    @ColumnInfo(name = "l_set") val lSet: Int = 0,
    @ColumnInfo(name = "l_ordinal") val lOrdinal: Int,
    @ColumnInfo(name = "l_winner") var lWinner: Int? = null
)
