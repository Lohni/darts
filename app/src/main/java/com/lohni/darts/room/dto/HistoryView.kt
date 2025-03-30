package com.lohni.darts.room.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.lohni.darts.room.entities.Player
import java.time.LocalDateTime

data class GameSummaryShort(
    @ColumnInfo(name = "gameId") val gameId: Int,
    @ColumnInfo(name = "date") val date: LocalDateTime,
    @ColumnInfo(name = "gameMode") val gameMode: String,
    @ColumnInfo(name = "sets") val sets: Int,
    @ColumnInfo(name = "legs") val legs: Int,
    @ColumnInfo(name = "playerCount") val playerCount: Int,
    @Embedded val winner: Player
)

data class ShortSummary(
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "ordinal") val ordinal: Int,
    @ColumnInfo(name = "winnerAvg") val winnerAvg: Float,
    @Embedded val winner: Player
)

data class HistoryView(
    @ColumnInfo("time_group") val timeGroup: String,
    @ColumnInfo("value") val value: Float
)
