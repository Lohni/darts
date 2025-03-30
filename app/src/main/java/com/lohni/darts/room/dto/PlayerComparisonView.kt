package com.lohni.darts.room.dto

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.Leg
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Set

data class PlayerComparisonView(
    @ColumnInfo("games_total") val gamesTotal: Int,
    @ColumnInfo("games_won") val gamesWon: Int,
    @ColumnInfo("sets_total") val setsTotal: Int,
    @ColumnInfo("sets_won") val setsWon: Int,
    @ColumnInfo("legs_total") val legsTotal: Int,
    @ColumnInfo("legs_won") val legsWon: Int,
    @Embedded val player: Player?
)

data class GameSetView(
    @Embedded val game: Game,
    @Relation(
        entity = Set::class,
        parentColumn = "g_id",
        entityColumn = "s_game"
    )
    val sets: List<SetLegView>
)

data class SetLegView(
    @Embedded val set: Set,
    @Relation(
        entity = Leg::class,
        parentColumn = "s_id",
        entityColumn = "l_set"
    )
    val legs: List<Leg>
)