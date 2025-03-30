package com.lohni.darts.game

import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType

class ClassicState(
    ordinal: Int,
    gameConfigurationView: GameConfigurationView,
    playerToStart: Int,
    players: List<Player>,
    onFinish: (LegState, Player) -> Unit
) : LegState(
    ordinal, playerToStart, gameConfigurationView, players, onFinish
) {
    private val gameModeConfig = gameConfigurationView.gameModeConfig.gameModeConfig

    override fun createThrow(field: Field, fieldType: FieldType, currScore: Float): Throw {
        val throwScore = calcThrowScore(field, fieldType)
        val isBust = checkBust(throwScore, currScore, fieldType)
        return createThrow(
            field,
            fieldType,
            throwScore,
            isBust,
            null
        )
    }

    override fun checkLegFinished(): Boolean {
        return scoreState[currTurn.player] == 0f
    }

    override fun handleStepState(turnFinished: Boolean) {
        //Ignored
    }

    override fun createTurnState(player: Player): TurnState {
        return TurnState(player)
    }

    override fun calculateScore(currScore: Float, diff: Float, undo: Boolean): Float {
        return if (undo) {
            currScore + diff
        } else {
            currScore - diff
        }
    }

    override fun calcWinner(): Player {
        return currTurn.player
    }

    private fun calcThrowScore(field: Field, fieldType: FieldType): Float {
        return if (fieldType == FieldType.ALL) {
            field.fId.toFloat()
        } else {
            (field.fId * fieldType.ftId).toFloat()
        }
    }

    private fun checkBust(
        throwScore: Float,
        currScore: Float,
        fieldType: FieldType
    ): Boolean {
        if (throwScore > currScore) return true
        if (0f < currScore - throwScore && currScore - throwScore < gameModeConfig.gmcCheckOut.ftId) return true

        return currScore == throwScore
                && gameModeConfig.gmcCheckOut != FieldType.ALL
                && gameModeConfig.gmcCheckOut != fieldType
    }
}
