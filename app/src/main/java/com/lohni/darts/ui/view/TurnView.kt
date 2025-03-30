package com.lohni.darts.ui.view

import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.enums.GameModeType

data class TurnView(
    val turn: Int,
    val startScore: Float,
    val throws: MutableList<Throw>,
    val player: Player,
    val gameModeType: GameModeType
)
