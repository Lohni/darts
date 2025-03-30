package com.lohni.darts.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "game_player", primaryKeys = ["gp_game", "gp_player"],
    indices = [Index("gp_game"), Index("gp_player")],
    foreignKeys = [ForeignKey(
        entity = Game::class,
        parentColumns = ["g_id"],
        childColumns = ["gp_game"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Player::class,
        parentColumns = ["p_id"],
        childColumns = ["gp_player"]
    )]
)
data class GamePlayer(
    @ColumnInfo(name = "gp_game") val gpGame: Int,
    @ColumnInfo(name = "gp_player") val gpPlayer: Int
)
