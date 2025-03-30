package com.lohni.darts.game

import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.ui.view.GameView
import com.lohni.darts.ui.view.PlayerView
import kotlin.random.Random

class GameState(
    private val numSets: Int,
    private val numLegs: Int,
    val gameConfigurationView: GameConfigurationView,
    private val players: List<Player>,
    private val onFinish: (State, Player) -> Unit,
    randomOrder: Boolean = false
) : State {
    val state = mutableMapOf<Player, MutableList<SetState>>()

    private var playerIndex: Int
    private var currSet: SetState
    private var setOrdinal = 0

    init {
        for (player in players) {
            state[player] = mutableListOf()
        }

        playerIndex = if (randomOrder) Random.nextInt(0, players.count()) else 0
        currSet = SetState(
            setOrdinal++,
            numLegs,
            gameConfigurationView,
            playerIndex,
            players,
            onLegFinish = { l, p -> onFinish.invoke(l, p) },
            onFinish = { s, p -> onSetFinished(s, p) })
    }

    fun onThrow(field: Field, fieldType: FieldType): PlayerView {
        val playerView = currSet.onThrow(field, fieldType)
        getSetsForPlayerView(playerView)
        return playerView
    }

    fun undo() {
        currSet.undo()
    }

    fun getGameView(): GameView {
        val gameView = currSet.getGameView()
        getSetsForPlayerView(gameView.currPlayerView)
        gameView.otherPlayerState.forEach { getSetsForPlayerView(it) }
        return gameView
    }


    private fun onSetFinished(setState: SetState, player: Player) {
        state[player]?.add(setState)

        val setsWon = state[player]?.size ?: 0
        if (setsWon == numSets) {
            onFinish.invoke(this, player)
        } else {
            if (playerIndex == players.size - 1) playerIndex = 0
            else playerIndex += 1
            createSetState()
            onFinish.invoke(setState, player)
        }
    }

    private fun createSetState() {
        currSet = SetState(
            setOrdinal++,
            numLegs,
            gameConfigurationView,
            playerIndex,
            players,
            onLegFinish = { l, p -> onFinish.invoke(l, p) },
            onFinish = { s, p -> onSetFinished(s, p) })
    }

    private fun getSetsForPlayerView(playerView: PlayerView) {
        playerView.setsWon = state[playerView.player]?.size ?: 0
    }
}