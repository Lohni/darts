package com.lohni.darts.room.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.lohni.darts.room.entities.Player

data class PlayerActivityView(
    @ColumnInfo("games_played") val gamesPlayed: Int,
    @ColumnInfo("darts_thrown") val dartsThrown: Int,
    @Embedded val player: Player
)
