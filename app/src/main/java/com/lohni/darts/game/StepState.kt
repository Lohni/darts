package com.lohni.darts.game

import com.lohni.darts.room.dto.GameConfigurationView
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.entities.Throw
import com.lohni.darts.room.entities.calculateScore
import com.lohni.darts.room.entities.requirementMet
import com.lohni.darts.room.entities.shortString
import com.lohni.darts.room.enums.Field
import com.lohni.darts.room.enums.FieldType
import com.lohni.darts.room.enums.StepWinCondition
import com.lohni.darts.ui.view.PlayerView

class StepState(
    ordinal: Int,
    gameConfigurationView: GameConfigurationView,
    playerToStart: Int,
    players: List<Player>,
    onFinish: (LegState, Player) -> Unit
) : LegState(
    ordinal, playerToStart, gameConfigurationView, players, onFinish
) {
    private val gmcView = gameConfigurationView.gameModeConfig
    private val steps: List<GameModeStep>

    private val stepState = mutableMapOf<Player, GameModeStep>()
    private val playersFinished = mutableListOf<Player>()
    private var requirementMet = false

    init {
        steps = if (gameConfigurationView.gameModeConfig.gameModeConfig.gmcRandomStepOrder) {
            gameConfigurationView.gameMode.steps.shuffled()
        } else {
            gameConfigurationView.gameMode.steps.sortedBy { it.gmsOrdinal }
        }

        if (steps.isNotEmpty()) {
            val firstStep = steps.first()
            players.forEach {
                stepState[it] = firstStep
            }
        } else {
            throw IllegalStateException()
        }
    }

    override fun createThrow(field: Field, fieldType: FieldType, currScore: Float): Throw {
        val step = stepState[getCurrPlayer()]
        val throwScore = calcThrowScore(field, fieldType, currScore)
        return createThrow(
            field,
            fieldType,
            throwScore,
            false,
            step
        )
    }

    private fun calcThrowScore(field: Field, fieldType: FieldType, currScore: Float): Float {
        if (getCurrentStep().requirementMet(field, fieldType)) {
            val scoredPoints = field.fId * fieldType.ftId
            requirementMet = true
            return gmcView.successCalculation?.calculateScore(currScore, scoredPoints.toFloat(), 0f)
                ?: 0f
        } else if (currTurn.countDarts() == 2 && !requirementMet) {
            return gmcView.failureCalculation?.calculateScore(currScore, 0f, 0f) ?: 0f
        }

        return 0f
    }

    override fun handleStepState(turnFinished: Boolean) {
        if (!turnFinished) {
            if (requirementMet && gmcView.gameModeConfig.gmcImmediateProceedOnSuccess) {
                setNextStep()
            }
        } else if (requirementMet || !gmcView.gameModeConfig.gmcRepeatOnFailure) {
            setNextStep()
        }
    }

    override fun calculateScore(currScore: Float, throwScore: Float, undo: Boolean): Float {
        return if (undo) currScore - throwScore
        else currScore + throwScore
    }

    override fun checkLegFinished(): Boolean {
        val playOutAllPlayers =
            gmcView.gameModeConfig.gmcStepWinCondition != StepWinCondition.FIRST_TO_FINISH
        return if (playOutAllPlayers) {
            playersFinished.size == players.size
        } else {
            playersFinished.size > 0
        }
    }

    override fun createTurnState(player: Player): TurnState {
        return TurnState(player, stepState[player])
    }

    override fun calcWinner(): Player {
        var winner = playersFinished.first()

        if (gmcView.gameModeConfig.gmcStepWinCondition == StepWinCondition.HIGHEST_SCORE) {
            for (p in playersFinished) {
                if ((scoreState[p] ?: 0f) > (scoreState[winner] ?: 0f)) {
                    winner = p
                }
            }
        } else if (gmcView.gameModeConfig.gmcStepWinCondition == StepWinCondition.LOWEST_SCORE) {
            for (p in playersFinished) {
                if ((scoreState[p] ?: 0f) < (scoreState[winner] ?: 0f)) {
                    winner = p
                }
            }
        }

        return winner
    }

    override fun undo() {
        playersFinished.remove(getCurrPlayer())
        super.undo()
        val gmsId = currTurn.getLastThrow()?.tGameModeStep ?: currTurn.step?.gmsId
        gmsId?.let { stepId ->
            stepState[getCurrPlayer()] =
                gameConfigurationView.gameMode.steps.first { it.gmsId == stepId }
        }

        //Recheck requirementMet
        requirementMet = false
        currTurn.getThrowsAsList().forEach {
            if (getCurrentStep().requirementMet(it.tField, it.tFieldType)) {
                requirementMet = true
            }
        }

        if (gmcView.gameModeConfig.gmcImmediateProceedOnSuccess) {
            currTurn.getLastThrow()?.let {
                if (getCurrentStep().requirementMet(it.tField, it.tFieldType)) {
                    setNextStep()
                }
            }
        }
    }

    override fun createCurrPlayerView(): PlayerView {
        val playerView = super.createCurrPlayerView()
        return playerView.copy(
            stepRequirement = stepState[playerView.player]?.shortString() ?: "",
            stepOrdinal = getStepStateAsString(playerView.player)
        )
    }

    private fun setNextStep() {
        val currPlayer = getCurrPlayer()
        val currStepIndex = steps.indexOf(stepState[currPlayer])

        if (currStepIndex == steps.size - 1) {
            playersFinished.add(getCurrPlayer())
        } else {
            stepState[currPlayer] = steps[currStepIndex + 1]
        }
        requirementMet = false
    }

    private fun getCurrentStep(): GameModeStep {
        return stepState[getCurrPlayer()] ?: GameModeStep()
    }

    private fun getStepStateAsString(player: Player): String {
        val currStep = stepState[player]
        val stepIndex = currStep?.let {
            gameConfigurationView.gameMode.steps.indexOf(it) + 1
        } ?: 0
        return "Step $stepIndex/${steps.size}"
    }
}