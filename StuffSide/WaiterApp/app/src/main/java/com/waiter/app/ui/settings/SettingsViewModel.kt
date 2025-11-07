package com.waiter.app.ui.settings

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.datastore.preferences.preferencesDataStore

private val Application.dataStore by preferencesDataStore(name = "settings")

class SettingsViewModel(app: Application): AndroidViewModel(app) {

    companion object {
        // --- Старі ключі (можна видалити waiterId, якщо він більше не потрібен) ---
        private val KEY_WAITER_ID = stringPreferencesKey("waiter_id")

        // --- Нові ключі для автентифікації ---
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
    }

    // Старий Flow (можна видалити)
    val waiterIdFlow = app.dataStore.data.map { p: Preferences ->
        p[KEY_WAITER_ID] ?: "waiter-001"
    }

    // Новий Flow для перевірки стану логіну
    val isLoggedInFlow = app.dataStore.data.map { p: Preferences ->
        p[KEY_IS_LOGGED_IN] ?: false
    }

    // Новий Flow для отримання імені юзера (наприклад, для UI)
    val usernameFlow = app.dataStore.data.map { p: Preferences ->
        p[KEY_USERNAME] ?: "Waiter"
    }

    // Старий метод (можна видалити)
    fun saveWaiterId(id: String) = viewModelScope.launch {
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_WAITER_ID] = id
        }
    }

    // --- Нові методи ---

    /**
     * Зберігає дані сесії після успішного логіну
     */
    fun saveLoginSession(userId: Int, username: String) = viewModelScope.launch {
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_IS_LOGGED_IN] = true
            p[KEY_USER_ID] = userId
            p[KEY_USERNAME] = username
        }
    }

    /**
     * Очищує сесію при виході
     */
    fun logout() = viewModelScope.launch {
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_IS_LOGGED_IN] = false
            p[KEY_USER_ID] = -1
            p[KEY_USERNAME] = ""
            // p.remove(KEY_WAITER_ID) // Також очистити старий ID
        }
    }
}