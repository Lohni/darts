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
import com.lohni.darts.room.dao.PlayerDao
import com.lohni.darts.room.dao.StatisticsDao
import com.lohni.darts.room.entities.GameMode
import com.lohni.darts.room.entities.GameModeConfig
import com.lohni.darts.room.entities.Player
import com.lohni.darts.ui.screens.game.GameRoute
import com.lohni.darts.ui.view.PlayerAverageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GameSelectionViewModel(
    private val playerDao: PlayerDao,
    private val gameModeDao: GameModeDao,
    private val statisticsDao: StatisticsDao
) : ViewModel() {
    val availablePlayers = mutableStateOf<List<Player>>(emptyList())
    val allGameModes = gameModeDao.getGameModes()

    val numSets = mutableIntStateOf(1)
    val numLegs = mutableIntStateOf(1)
    val randomOrder = mutableStateOf(false)
    val selectedPlayers = mutableStateListOf<PlayerAverageView>()
    val selectedGameMode = mutableStateOf(GameMode(gmName = "Select Mode"))

    private var gameModeConfig = GameModeConfig()

    init {
        viewModelScope.launch {
            gameModeDao.getLastPlayedGameMode().first {
                if (it != null) {
                    selectedGameMode.value = it
                }
                true
            }

            playerDao.getAllPlayers().first { all ->
                availablePlayers.value = all
                playerDao.getLastUsedPlayers().first { players ->
                    players.forEach { p ->
                        statisticsDao.getPlayerAverageForGameMode(
                            selectedGameMode.value.gmId,
                            p.pId
                        ).first {
                            selectedPlayers.add(PlayerAverageView(p, it))
                        }
                    }

                    val newList = availablePlayers.value.toMutableList()
                    newList.removeAll(players)
                    availablePlayers.value = newList.toList()
                    true
                }
                true
            }
        }
    }

    fun setGameMode(gameMode: GameMode) {
        selectedGameMode.value = gameMode

        viewModelScope.launch {
            val tmpCopy = selectedPlayers.toList()
            selectedPlayers.clear()
            tmpCopy.forEach { p ->
                statisticsDao.getPlayerAverageForGameMode(
                    selectedGameMode.value.gmId,
                    p.player.pId
                ).first {
                    selectedPlayers.add(PlayerAverageView(p.player, it))
                }
            }

            gameModeDao.getGameModeConfigById(gameMode.gmConfig).first {
                gameModeConfig = it
                true
            }
        }
    }

    fun changePlayer(index: Int, player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            statisticsDao.getPlayerAverageForGameMode(selectedGameMode.value.gmId, player.pId)
                .first {
                    val prev = selectedPlayers[index]
                    selectedPlayers[index] = PlayerAverageView(player, it)

                    val newList = availablePlayers.value.toMutableList()
                    newList.remove(player)
                    newList.add(prev.player)

                    availablePlayers.value = newList.toList()
                    true
                }
        }
    }

    fun addPlayer() {
        if (availablePlayers.value.isNotEmpty()) {
            val player = availablePlayers.value.first()
            viewModelScope.launch(Dispatchers.IO) {
                statisticsDao.getPlayerAverageForGameMode(selectedGameMode.value.gmId, player.pId)
                    .first {
                        selectedPlayers.add(PlayerAverageView(player, it))
                        val newList = availablePlayers.value.toMutableList()
                        newList.remove(player)
                        availablePlayers.value = newList.toList()
                        true
                    }
            }
        }
    }

    fun removePlayer(index: Int) {
        val removed = selectedPlayers.removeAt(index)
        val newList = availablePlayers.value.toMutableList()
        newList.add(removed.player)
        availablePlayers.value = newList.toList()
    }

    fun increaseSets() {
        numSets.value += 1
    }

    fun decreaseSets() {
        if (numSets.intValue > 1) {
            numSets.value -= 1
        }
    }

    fun increaseLegs() {
        numLegs.value += 1
    }

    fun decreaseLegs() {
        if (numLegs.intValue > 1) {
            numLegs.value -= 1
        }
    }

    fun toggleRandomOrder() {
        randomOrder.value = !randomOrder.value
    }

    fun play(): GameRoute? {
        if (selectedGameMode.value.gmId == 0 || selectedPlayers.isEmpty()) {
            return null
        }
        return GameRoute(
            gameMode = selectedGameMode.value.gmId,
            numSets = numSets.intValue,
            numLegs = numLegs.intValue,
            randomOrder = randomOrder.value,
            playerIds = selectedPlayers.map(PlayerAverageView::player).map(Player::pId)
        )
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return GameSelectionViewModel(
                    AppDatabase.getInstance(application.applicationContext).playerDao(),
                    AppDatabase.getInstance(application.applicationContext).gameModeDao(),
                    AppDatabase.getInstance(application.applicationContext).statisticsDao()
                ) as T
            }
        }
    }
}