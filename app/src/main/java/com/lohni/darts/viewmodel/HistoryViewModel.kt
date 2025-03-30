package com.lohni.darts.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.HistoryDao
import com.lohni.darts.room.dto.GameSummaryShort
import com.lohni.darts.room.dto.ShortSummary
import com.lohni.darts.room.enums.GameModeType
import com.lohni.darts.ui.view.TurnView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryViewModel(val historyDao: HistoryDao) : ViewModel() {
    val games = historyDao.getShortGameSummaries()

    val game = mutableStateOf<GameSummaryShort?>(null)
    val setSummaries = mutableStateListOf<ShortSummary>()
    val legSummaries = mutableStateListOf<ShortSummary>()
    val turnSummaries = mutableStateListOf<TurnView>()

    fun setGameAndSetId(gameId: Int) {
        viewModelScope.launch {
            historyDao.getShortGameSummary(gameId).first {
                game.value = it
                true
            }
            historyDao.getShortSetSummary(gameId).first {
                setSummaries.clear()
                setSummaries.addAll(it)
            }
        }
    }

    fun setGameAndSetId(gameId: Int, setId: Int) {
        viewModelScope.launch {
            historyDao.getShortGameSummary(gameId).first {
                game.value = it
                true
            }
            historyDao.getShortLegSummary(setId).first {
                legSummaries.clear()
                legSummaries.addAll(it)
            }
        }
    }

    fun setGameAndLegId(gameId: Int, legId: Int) {
        viewModelScope.launch {
            historyDao.getShortGameSummary(gameId).first {
                game.value = it
                true
            }

            historyDao.getGameModeByGameId(gameId).first { gm ->
                historyDao.getPlayers(gameId).first { players ->
                    historyDao.getThrowsByLeg(legId).first { throws ->
                        val scoreMap =
                            players.associateWith { gm.gmStartScore.toFloat() }.toMutableMap()
                        val turnViews = mutableListOf<TurnView>()

                        val firstThrow = throws.first()
                        var currPlayer = players.first { it.pId == firstThrow.tPlayer }
                        var turnView = TurnView(
                            firstThrow.tTurn,
                            scoreMap[currPlayer] ?: -1f,
                            mutableListOf(),
                            currPlayer,
                            gm.gmType
                        )
                        throws.drop(1)
                        throws.forEach { t ->
                            if (t.tTurn != turnView.turn || t.tPlayer != turnView.player.pId) {
                                if (!turnView.throws.any { it.tBust }) {
                                    if (gm.gmType == GameModeType.CLASSIC) {
                                        scoreMap[currPlayer] = (scoreMap[currPlayer] ?: -1f) - turnView.throws.sumOf { it.tScore.toDouble() }.toFloat()
                                    } else {
                                        scoreMap[currPlayer] = (scoreMap[currPlayer] ?: 1f) + turnView.throws.sumOf { it.tScore.toDouble() }.toFloat()
                                    }
                                }
                                turnViews.add(turnView)
                                currPlayer = players.first { it.pId == t.tPlayer }
                                turnView = TurnView(
                                    t.tTurn,
                                    scoreMap[currPlayer] ?: -1f,
                                    mutableListOf(),
                                    currPlayer,
                                    gm.gmType
                                )
                            }
                            turnView.throws.add(t)
                        }
                        turnViews.add(turnView)
                        turnSummaries.addAll(turnViews)
                    }
                    true
                }
                true
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return HistoryViewModel(
                    AppDatabase.getInstance(application.applicationContext).historyDao(),
                ) as T
            }
        }
    }
}