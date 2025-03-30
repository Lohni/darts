package com.lohni.darts.ui.view

import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw


data class GameView(
    val currPlayerView: PlayerView,
    val otherPlayerState: List<PlayerView> = emptyList()
)

data class PlayerView(
    val player: Player,
    val score: Float,
    val average: Float,
    val darts: Int,
    val throwOne: Throw?,
    val throwTwo: Throw?,
    val throwThree: Throw?,
    var legsWon: Int = 0,
    var setsWon: Int = 0,
    val nextPlayer: Player? = null,
    val nextPlayerPoints: Float? = null,
    val stepRequirement: String = "",
    val stepOrdinal: String = ""
)

fun PlayerView.getThrows(): List<Throw> {
    val list = mutableListOf<Throw>()
    throwOne?.let { list.add(it) }
    throwTwo?.let { list.add(it) }
    throwThree?.let { list.add(it) }
    return list
}

