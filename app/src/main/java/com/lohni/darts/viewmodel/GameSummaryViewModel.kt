package com.lohni.darts.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.GameDao
import com.lohni.darts.room.dto.GameSummary
import com.lohni.darts.room.entities.Game
import com.lohni.darts.room.entities.Player
import com.lohni.darts.ui.screens.game.GameRoute
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameSummaryViewModel(private val gameDao: GameDao) : ViewModel() {
    var randomPlayerOrder = false
    private val playerIds = mutableListOf<Int>()
    val gameSummaryList = mutableStateListOf<GameSummary>()
    val winner = mutableStateOf(Player(pName = ""))
    val game = mutableStateOf(Game())

    fun setGameId(gId: Int) {
        viewModelScope.launch {
            gameDao.getWinningPlayerByGameId(gId).first {
                winner.value = it
                true
            }

            gameDao.getGameById(gId).first {
                game.value = it
                true
            }

            gameDao.getGamePlayerIdsByGameId(gId).first { pIds ->
                playerIds.addAll(pIds)
                gameSummaryList.clear()
                pIds.forEach { pId ->
                    gameDao.getGameSummary(gId, pId).first { gameSummary ->
                        val list = gameSummaryList.toMutableList()
                        list.add(gameSummary)
                        gameSummaryList.clear()
                        gameSummaryList.addAll(list.sortedByDescending { it.setsWon })
                        true
                    }
                }
                true
            }
        }
    }

    fun setGameAndSetId(gId: Int, sId: Int) {
        viewModelScope.launch {
            gameDao.getWinningPlayerBySetId(sId).first {
                winner.value = it
                true
            }

            gameDao.getGameById(gId).first {
                game.value = it
                true
            }

            gameDao.getGamePlayerIdsByGameId(gId).first { pIds ->
                gameSummaryList.clear()
                pIds.forEach { pId ->
                    gameDao.getGameSetSummary(sId, pId).first { gameSummary ->
                        val list = gameSummaryList.toMutableList()
                        list.add(gameSummary)
                        gameSummaryList.clear()
                        gameSummaryList.addAll(list.sortedByDescending { it.legsWon })
                        true
                    }
                }
                true
            }
        }
    }

    fun setGameAndLegId(gId: Int, lId: Int) {
        viewModelScope.launch {
            gameDao.getGameLegSummary(gId, lId).first { gameSummary ->
                gameSummaryList.clear()
                gameSummaryList.addAll(gameSummary)
            }
        }
    }

    fun getGameRouteForRepeat(): GameRoute {
        return GameRoute(
            game.value.gGameMode,
            game.value.gSets,
            game.value.gLegs,
            playerIds,
            randomPlayerOrder
        )
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return GameSummaryViewModel(
                    AppDatabase.getInstance(application.applicationContext).gameDao(),
                ) as T
            }
        }
    }
}