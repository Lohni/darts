package com.lohni.darts.game

import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.ui.view.GameView
import com.lohni.darts.ui.view.PlayerView

abstract class LegState(
    val ordinal: Int,
    playerToStart: Int,
    protected val gameConfigurationView: GameConfigurationView,
    val players: List<Player>,
    private val onFinish: (LegState, Player) -> Unit
) : State {
    val scoreState = mutableMapOf<Player, Float>()
    var playerIndex = playerToStart

    val turnList = mutableListOf<TurnState>()
    protected var currTurn =
        TurnState(players[playerIndex], gameConfigurationView.gameMode.steps.firstOrNull())

    init {
        for (player in players) {
            scoreState[player] = gameConfigurationView.gameMode.gameMode.gmStartScore.toFloat()
        }
    }

    open fun onThrow(field: Field, fieldType: FieldType): PlayerView {
        if (field == Field.TWENTYFIVE && fieldType == FieldType.Triple) {
            return createCurrPlayerView()
        }

        val currScore = scoreState[currTurn.player] ?: 0f

        //Create Throw
        val newThrow = createThrow(field, fieldType, currScore)
        currTurn.addThrow(newThrow)

        //Step state
        handleStepState(currTurn.finished)

        //Score state
        if (!currTurn.busted) scoreState[currTurn.player] =
            calculateScore(currScore, newThrow.tScore, false)
        else scoreState[currTurn.player] = calculateScore(currScore, currTurn.turnScore, true)

        val playerView = createCurrPlayerView()

        if (checkLegFinished()) {
            finishTurn(true)
            onFinish.invoke(this, calcWinner())
        } else if (currTurn.finished) {
            finishTurn(false)
        }

        return playerView
    }

    private fun finishTurn(legFinished: Boolean) {
        turnList.add(currTurn)

        if (!legFinished) {
            playerIndex = nextPlayerIndex()
            currTurn = createTurnState(players[playerIndex])
        }
    }

    protected abstract fun checkLegFinished(): Boolean

    protected abstract fun handleStepState(turnFinished: Boolean)

    protected abstract fun createTurnState(player: Player): TurnState

    protected abstract fun calculateScore(currScore: Float, throwScore: Float, undo: Boolean): Float

    protected abstract fun calcWinner(): Player

    protected abstract fun createThrow(field: Field, fieldType: FieldType, currScore: Float): Throw

    protected fun createThrow(
        field: Field,
        fieldType: FieldType,
        throwScore: Float,
        isBust: Boolean,
        step: GameModeStep?
    ): Throw {
        val turnCount = turnList.stream().filter { it.player.pId == currTurn.player.pId }.count()
        val throwOrdinal = turnList.count() * 3 + (currTurn.countDarts())
        return Throw(
            tPlayer = currTurn.player.pId,
            tTurn = turnCount.toInt(),
            tOrdinal = throwOrdinal,
            tField = field,
            tFieldType = fieldType,
            tScore = throwScore,
            tBust = isBust,
            tGameModeStep = step?.gmsId
        )
    }

    open fun undo() {
        var undoneScore = currTurn.undo()
        if (undoneScore >= 0) {
            scoreState[currTurn.player] =
                calculateScore(scoreState[currTurn.player] ?: 0f, undoneScore, true)
        } else if (turnList.isNotEmpty()) {
            currTurn = turnList.removeAt(turnList.lastIndex)
            undoneScore = currTurn.undo()
            playerIndex = previousPlayerIndex()
            if (currTurn.busted) scoreState[currTurn.player] =
                calculateScore((scoreState[currTurn.player] ?: 0f), currTurn.turnScore, false)
            else scoreState[currTurn.player] =
                calculateScore(scoreState[currTurn.player] ?: 0f, undoneScore, true)
        }
    }

    fun getCurrPlayer(): Player {
        return players[playerIndex]
    }

    private fun nextPlayerIndex(): Int {
        var index = playerIndex
        if (index == players.size - 1) index = 0
        else index += 1
        return index
    }

    private fun previousPlayerIndex(): Int {
        var index = playerIndex - 1
        if (index < 0) index = players.size - 1
        return index
    }

    /**
     * GAME VIEW
     */
    fun getGameView(): GameView {
        return GameView(
            createCurrPlayerView(),
            createNonActivePlayerViews()
        )
    }

    protected open fun createCurrPlayerView(): PlayerView {
        val playerThrows =
            turnList.filter { it.player.pId == currTurn.player.pId }
                .flatMap(TurnState::getThrowsAsList).toMutableList()

        playerThrows.addAll(currTurn.getThrowsAsList())

        val darts = playerThrows.count()
        val score = scoreState[currTurn.player] ?: 0f
        val average =
            if (darts == 0) 0f else (playerThrows.sumOf { it.tScore.toDouble() } / darts) * 3

        val nextPlayerIndex = nextPlayerIndex()
        var nextPlayer: Player? = null
        var nextPlayerPoints: Float? = null
        if (nextPlayerIndex != playerIndex) {
            nextPlayer = players[nextPlayerIndex]
            nextPlayerPoints = scoreState[nextPlayer]
        }

        return PlayerView(
            currTurn.player,
            score,
            average.toFloat(),
            darts,
            currTurn.throwOne,
            currTurn.throwTwo,
            currTurn.throwThree,
            nextPlayer = nextPlayer,
            nextPlayerPoints = nextPlayerPoints
        )
    }

    private fun createNonActivePlayerViews(): List<PlayerView> {
        val list = mutableListOf<PlayerView>()
        if (players.size <= 1) return list

        var nextPlayerIndex = nextPlayerIndex()
        while (nextPlayerIndex != playerIndex) {
            val player = players[nextPlayerIndex]
            val playerThrows = turnList.filter { it.player.pId == player.pId }
            val throws = playerThrows.count()
            val darts = throws * 3
            val score = scoreState[player] ?: 0f
            val average =
                if (darts == 0) 0f else (playerThrows.sumOf { it.turnScore.toDouble() } / darts) * 3
            list.add(
                PlayerView(
                    player,
                    score,
                    average.toFloat(),
                    darts,
                    null,
                    null,
                    null
                )
            )
            nextPlayerIndex += 1
            if (nextPlayerIndex > players.size - 1) nextPlayerIndex = 0
        }
        return list
    }
}