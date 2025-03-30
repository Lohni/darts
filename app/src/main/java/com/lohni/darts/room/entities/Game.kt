package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "game",
    indices = [Index("g_game_mode")],
    foreignKeys = [ForeignKey(
        entity = GameMode::class,
        parentColumns = ["gm_id"],
        childColumns = ["g_game_mode"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Player::class,
        parentColumns = ["p_id"],
        childColumns = ["g_winner"]
    )]
)
data class Game(
    @ColumnInfo(name = "g_id") @PrimaryKey(autoGenerate = true) var gId: Int = 0,
    @ColumnInfo(name = "g_credat") val gCredat: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "g_num_sets") val gSets: Int = 1,
    @ColumnInfo(name = "g_num_legs") val gLegs: Int = 1,
    @ColumnInfo(name = "g_game_mode") var gGameMode: Int = 0,
    @ColumnInfo(name = "g_winner") val gWinner: Int = 0
)
