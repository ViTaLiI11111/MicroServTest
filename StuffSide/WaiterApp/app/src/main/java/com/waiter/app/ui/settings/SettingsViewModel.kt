package com.waiter.app.ui.settings

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.datastore.preferences.preferencesDataStore

private val Application.dataStore by preferencesDataStore(name = "settings")

class SettingsViewModel(app: Application): AndroidViewModel(app) {

    companion object {
        private val KEY_WAITER_ID = stringPreferencesKey("waiter_id")
        // за бажанням: KEY_BASE_URL, щоб міняти API без перекомпіляції
    }

    val waiterIdFlow = app.dataStore.data.map { p: Preferences ->
        p[KEY_WAITER_ID] ?: "waiter-001"
    }

    fun saveWaiterId(id: String) = viewModelScope.launch {
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_WAITER_ID] = id
        }
    }
}
