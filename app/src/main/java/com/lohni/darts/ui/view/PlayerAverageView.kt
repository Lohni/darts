package com.lohni.darts.ui.view

import com.lohni.darts.room.entities.Player

data class PlayerAverageView(
    val player: Player,
    val average: Float
)