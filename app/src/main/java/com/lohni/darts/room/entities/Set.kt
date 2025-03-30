package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "set",
    indices = [Index("s_game")],
    foreignKeys = [ForeignKey(
        entity = Game::class,
        parentColumns = ["g_id"],
        childColumns = ["s_game"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Player::class,
        parentColumns = ["p_id"],
        childColumns = ["s_winner"]
    )]
)
data class Set(
    @ColumnInfo(name = "s_id") @PrimaryKey(autoGenerate = true) val sId: Int = 0,
    @ColumnInfo(name = "s_game") val sGame: Int = 0,
    @ColumnInfo(name = "s_ordinal") val sOrdinal: Int,
    @ColumnInfo(name = "s_winner") var sWinner: Int? = null
)
