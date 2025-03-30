package com.lohni.darts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.HistoryDao

class HomeViewModel(
    val historyDao: HistoryDao
): ViewModel() {

    val games = historyDao.getShortGameSummaries()

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return HomeViewModel(
                    AppDatabase.getInstance(application.applicationContext).historyDao(),
                ) as T
            }
        }
    }
}