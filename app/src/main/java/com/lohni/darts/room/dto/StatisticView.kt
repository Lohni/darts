package com.lohni.darts.room.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import java.time.LocalDateTime

data class GameSummary(
    @Embedded val player: Player,
    @ColumnInfo("setsWon") val setsWon: Int,
    @ColumnInfo("legsWon") val legsWon: Int,
    @ColumnInfo("score") val score: Float,
    @ColumnInfo("avg") val average: Float
)

data class CompetitiveSummary(
    @ColumnInfo("amount") val amount: Int,
    @ColumnInfo("won") val won: Int
)

data class LegGroupedThrows(
    @ColumnInfo("game_date") val gameDate: LocalDateTime,
    @ColumnInfo("has_won") val hasWon: Boolean,
    @ColumnInfo("leg") val leg: Int,
    @Relation(parentColumn = "leg", entityColumn = "t_leg") val throws: List<Throw>
)