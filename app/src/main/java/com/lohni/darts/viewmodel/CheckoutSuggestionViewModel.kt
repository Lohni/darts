package com.lohni.darts.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.CheckoutDao
import com.lohni.darts.room.entities.CheckoutTable
import com.lohni.darts.room.entities.Player
import com.lohni.darts.room.enums.FieldType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class CheckoutSuggestionViewModel(private val checkoutDao: CheckoutDao) : ViewModel() {

    val DEFAULT_PLAYER = Player(-2, "Default")
    var selectedPlayer = mutableStateOf(DEFAULT_PLAYER)
    var playersWithoutSuggestion = mutableStateListOf<Player>()
    var availablePlayers = mutableStateListOf(DEFAULT_PLAYER)
    var suggestions = mutableStateListOf<CheckoutTable>()

    init {
        getSuggestions(FieldType.Double)
    }

    fun changeCheckoutType(checkoutType: FieldType) {
        getSuggestions(checkoutType)
    }

    fun changePlayer(checkoutType: FieldType, old: Player, new: Player) {
        selectedPlayer.value = new

        availablePlayers.remove(new)
        availablePlayers.add(old)

        viewModelScope.launch {
            checkoutDao.getCheckoutTables(checkoutType, new.pId).first {
                suggestions.clear()
                suggestions.addAll(it)
            }
        }
    }

    fun replaceSuggestions(original: CheckoutTable, new: CheckoutTable) {
        val i = suggestions.indexOf(original)
        suggestions.removeAt(i)
        suggestions.add(i, new)
        viewModelScope.launch(Dispatchers.IO) {
            checkoutDao.updateCheckoutTable(new)
        }
    }

    fun addSuggestionForPlayer(checkoutType: FieldType, player: Player) {
        playersWithoutSuggestion.remove(player)
        viewModelScope.launch(Dispatchers.IO) {
            checkoutDao.createSuggestionsForPlayer(checkoutType, player.pId)

            checkoutDao.getCheckoutTables(checkoutType, player.pId).first {
                suggestions.clear()
                suggestions.addAll(it)
            }
            selectedPlayer.value = player
        }
    }

    fun deletePlayerSuggestion(checkoutType: FieldType, player: Player) {
        viewModelScope.launch(Dispatchers.IO) {
            checkoutDao.deleteSuggestionForPlayer(checkoutType, player.pId)
        }
    }

    private fun getSuggestions(checkoutType: FieldType) {
        selectedPlayer.value = DEFAULT_PLAYER
        viewModelScope.launch {
            checkoutDao.getCheckoutTables(checkoutType, -2).first {
                suggestions.clear()
                suggestions.addAll(it)
            }
            checkoutDao.getPlayersForCheckoutType(checkoutType).first {
                availablePlayers.clear()
                availablePlayers.add(DEFAULT_PLAYER)
                availablePlayers.addAll(it)
            }
        }
        viewModelScope.launch {
            checkoutDao.getPlayersWithoutSuggestions(checkoutType).first {
                playersWithoutSuggestion.clear()
                playersWithoutSuggestion.addAll(it)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return CheckoutSuggestionViewModel(
                    AppDatabase.getInstance(application.applicationContext).checkoutDao()
                ) as T
            }
        }
    }
}