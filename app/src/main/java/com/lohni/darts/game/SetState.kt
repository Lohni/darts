package com.lohni.darts.game

import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.ui.view.GameView
import com.lohni.darts.ui.view.PlayerView

class SetState(
    val ordinal: Int,
    private val numLegs: Int,
    private val gameConfigurationView: GameConfigurationView,
    private val playerToStart: Int,
    private val players: List<Player>,
    private val onLegFinish: (LegState, Player) -> Unit,
    private val onFinish: (SetState, Player) -> Unit
) : State {
    val state = mutableMapOf<Player, MutableList<LegState>>()

    private var currLeg: LegState
    private var playerIndex = playerToStart
    private var legOrdinal = 0

    init {
        for (player in players) {
            state[player] = mutableListOf()
        }
        currLeg = getLegState()
    }

    private fun getLegState(): LegState {
        return if (gameConfigurationView.gameMode.gameMode.gmType == GameModeType.STEP) {
            StepState(
                legOrdinal++,
                gameConfigurationView,
                playerIndex,
                players,
                onFinish = { s, p -> onLegFinish(s, p) })
        } else {
            ClassicState(
                legOrdinal++,
                gameConfigurationView,
                playerIndex,
                players,
                onFinish = { s, p -> onLegFinish(s, p) })
        }
    }

    fun onThrow(field: Field, fieldType: FieldType): PlayerView {
        val playerView = currLeg.onThrow(field, fieldType)
        getLegsForPlayerView(playerView)
        return playerView
    }

    fun undo() {
        currLeg.undo()
    }

    fun getGameView(): GameView {
        val gameView = currLeg.getGameView()
        getLegsForPlayerView(gameView.currPlayerView)
        gameView.otherPlayerState.forEach { getLegsForPlayerView(it) }
        return gameView
    }

    private fun onLegFinish(legState: LegState, winner: Player) {
        state[winner]?.add(legState)

        val legsWon = state[winner]?.size ?: 0
        if (legsWon == numLegs) {
            onFinish.invoke(this, winner)
        } else {
            if (playerIndex == players.size - 1) playerIndex = 0
            else playerIndex += 1
            currLeg = getLegState()
            onLegFinish.invoke(legState, winner)
        }
    }

    private fun getLegsForPlayerView(playerView: PlayerView) {
        playerView.legsWon = state[playerView.player]?.size ?: 0
    }
}