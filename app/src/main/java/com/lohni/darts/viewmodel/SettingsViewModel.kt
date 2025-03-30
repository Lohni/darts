package com.lohni.darts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.lohni.darts.room.AppDatabase
import com.lohni.darts.room.dao.SettingsDao
import com.lohni.darts.room.entities.Setting
import com.lohni.darts.room.enums.SettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(private val settingsDao: SettingsDao) : ViewModel() {

    fun getDarkMode(): Flow<String> {
        return settingsDao.getSettingValue(SettingType.DARK_MODE)
    }

    fun toggleDarkMode() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsDao.getSetting(SettingType.DARK_MODE).first { setting ->
                setting.sValue = (!setting.sValue.toBooleanStrict()).toString()
                updateSetting(setting)
                true
            }
        }
    }

    fun updateSetting(setting: Setting) {
        settingsDao.updateSetting(setting)
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return SettingsViewModel(
                    AppDatabase.getInstance(application.applicationContext).settingsDao()
                ) as T
            }
        }
    }
}