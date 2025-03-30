package com.lohni.darts.viewmodel

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.GameModeDao
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.entities.GameModeStep
import com.lohni.darts.room.entities.ScoreCalculation
import com.lohni.darts.room.enums.GameModeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

class GameModeViewModel(private val gameModeDao: GameModeDao) : ViewModel() {
    private val gameModeStepsToDelete = mutableListOf<GameModeStep>()

    var gameModes = gameModeDao.getGameModes()
    var editable = mutableStateOf(true)

    var gameMode = mutableStateOf(GameMode())
    var gameModeConfig = mutableStateOf(GameModeConfig())
    var successCalculation = mutableStateOf(ScoreCalculation())
    var failureCalculation = mutableStateOf(ScoreCalculation())
    var gameModeSteps = mutableStateListOf<GameModeStep>()
    var associatedGames = mutableIntStateOf(0)

    fun setGameModeId(id: Int) {
        viewModelScope.launch {
            gameModeDao.getGameModeById(id).first { gm ->
                gameMode.value = gm

                gameModeDao.getGameModeConfigById(gm.gmConfig).first {
                    gameModeConfig.value = it

                    it.gmcSuccessCalculation?.let { scId ->
                        gameModeDao.getScoreCalculationById(scId).first { sc ->
                            successCalculation.value = sc
                            true
                        }
                    }

                    it.gmcFailureCalculation?.let { scId ->
                        gameModeDao.getScoreCalculationById(scId).first { sc ->
                            failureCalculation.value = sc
                            true
                        }
                    }
                    true
                }

                gameModeDao.getGameCountForGameMode(gm.gmId).first { count ->
                    associatedGames.intValue = count
                    true
                }

                if (gameMode.value.gmType == GameModeType.STEP) {
                    gameModeDao.getGameModeStepsForGameMode(gameMode.value.gmId).first {
                        gameModeSteps.addAll(it)
                    }
                }

                true
            }
        }
    }

    fun setGameMode(gameMode: GameMode) {
        this.gameMode.value = gameMode
    }

    fun setGameModeConfig(gameModeConfig: GameModeConfig) {
        this.gameModeConfig.value = gameModeConfig
    }

    fun setSuccessCalculation(calc: ScoreCalculation) {
        this.successCalculation.value = calc
    }

    fun setFailureCalculation(calc: ScoreCalculation) {
        this.failureCalculation.value = calc
    }

    fun createGameModeStep() {
        gameModeSteps.add(
            GameModeStep(
                gmsId = -abs(Random.nextInt(Int.MAX_VALUE)),
                gmsGameMode = 0,
                gmsOrdinal = gameModeSteps.size
            )
        )
    }

    fun saveState() {
        viewModelScope.launch(Dispatchers.IO) {
            if (successCalculation.value.scId == 0) {
                gameModeConfig.value.gmcSuccessCalculation = gameModeDao.createScoreCalculation(successCalculation.value).toInt()
            } else gameModeDao.updateScoreCalculation(successCalculation.value)

            if (failureCalculation.value.scId == 0) {
                gameModeConfig.value.gmcFailureCalculation = gameModeDao.createScoreCalculation(failureCalculation.value).toInt()
            } else gameModeDao.updateScoreCalculation(failureCalculation.value)

            if (gameModeConfig.value.gmcId == 0) {
                gameMode.value.gmConfig =
                    gameModeDao.createGameModeConfig(gameModeConfig.value).toInt()
            } else gameModeDao.updateGameModeConfig(gameModeConfig.value)

            var gameModeId = gameMode.value.gmId
            if (gameMode.value.gmId == 0) {
                gameModeId = gameModeDao.createGameMode(gameMode.value).toInt()
                gameMode.value.gmId = gameModeId
            } else gameModeDao.updateGameMode(gameMode.value)

            gameModeSteps.forEach {
                it.gmsGameMode = gameModeId
                if (it.gmsId <= 0) gameModeDao.createGameModeStep(it.copy(gmsId = 0))
                else gameModeDao.updateGameModeStep(it)
            }

            gameModeStepsToDelete.forEach {
                gameModeDao.deleteGameModeStep(it)
            }
        }
    }

    fun duplicate() {
        gameMode.value.gmName += "-Copy"
        gameMode.value.gmId = 0
        gameModeConfig.value.gmcId = 0
        successCalculation.value.scId = 0
        failureCalculation.value.scId = 0
        gameModeSteps.forEach{ it.gmsId = 0 }
        saveState()
    }

    fun deleteGameMode() {
        viewModelScope.launch(Dispatchers.IO) {
            gameModeDao.deleteGameMode(gameMode.value)
            gameModeDao.deleteGameModeConfig(gameModeConfig.value)
        }
    }

    fun deleteGameModeStep(step: GameModeStep) {
        val index = gameModeSteps.indexOf(step)
        for (i in index + 1..<gameModeSteps.size) {
            gameModeSteps[i] = gameModeSteps[i].copy(gmsOrdinal = i - 1)
        }
        gameModeSteps.removeAt(index)

        if (step.gmsId > 0) {
            gameModeStepsToDelete.add(step)
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return GameModeViewModel(
                    AppDatabase.getInstance(application.applicationContext).gameModeDao()
                ) as T
            }
        }
    }
}