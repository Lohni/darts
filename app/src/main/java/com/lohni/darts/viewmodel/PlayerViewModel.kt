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
import com.lohni.darts.room.dao.PlayerDao
import com.lohni.darts.room.dto.GameSummaryShort
import com.lohni.darts.room.entities.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerDao: PlayerDao, private val historyDao: HistoryDao) :
    ViewModel() {

    var players = playerDao.getAllPlayers()
    var player = mutableStateOf(Player(pName = ""))
    var playerGameHistory = mutableStateListOf<GameSummaryShort>()

    fun setPlayerContext(playerId: Int) {
        viewModelScope.launch {
            playerDao.getPlayerById(playerId).first { p ->
                player.value = p
                true
            }
            historyDao.getShortGameSummariesForPlayer(playerId).first { hist ->
                playerGameHistory.addAll(hist)
            }
        }
    }

    fun updatePlayer(changedPlayer: Player) {
        player.value = changedPlayer
    }

    fun deletePlayer() {
        viewModelScope.launch(Dispatchers.IO) {
            if (playerGameHistory.isNotEmpty()) playerDao.updatePlayer(player.value.copy(pArchive = 1))
            else playerDao.deletePlayer(player.value)
        }
    }

    fun saveState() {
        viewModelScope.launch(Dispatchers.IO) {
            if (player.value.pId == 0) playerDao.createPlayer(player.value)
            else playerDao.updatePlayer(player.value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return PlayerViewModel(
                    AppDatabase.getInstance(application.applicationContext).playerDao(),
                    AppDatabase.getInstance(application.applicationContext).historyDao()
                ) as T
            }
        }
    }
}