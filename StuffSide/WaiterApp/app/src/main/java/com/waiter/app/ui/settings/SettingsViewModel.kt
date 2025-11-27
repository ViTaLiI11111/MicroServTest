package com.waiter.app.ui.settings

import android.app.Application
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
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = intPreferencesKey("user_id")
        private val KEY_USERNAME = stringPreferencesKey("username")
        // НОВИЙ КЛЮЧ
        private val KEY_ROLE = stringPreferencesKey("user_role")
    }

    val isLoggedInFlow = app.dataStore.data.map { p -> p[KEY_IS_LOGGED_IN] ?: false }

    val userIdFlow = app.dataStore.data.map { p -> p[KEY_USER_ID] ?: 0 }

    val usernameFlow = app.dataStore.data.map { p -> p[KEY_USERNAME] ?: "Staff" }

    // Отримуємо роль (за замовчуванням WAITER, щоб не зламати стару логіку)
    val userRoleFlow = app.dataStore.data.map { p ->
        p[KEY_ROLE] ?: "WAITER"
    }

    // Оновлений метод збереження сесії
    fun saveLoginSession(userId: Int, username: String, role: String) = viewModelScope.launch {
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_IS_LOGGED_IN] = true
            p[KEY_USER_ID] = userId
            p[KEY_USERNAME] = username
            p[KEY_ROLE] = role // Зберігаємо роль
        }
    }

    fun logout() = viewModelScope.launch {
        getApplication<Application>().dataStore.edit { p ->
            p[KEY_IS_LOGGED_IN] = false
            p[KEY_USER_ID] = -1
            p[KEY_USERNAME] = ""
            p[KEY_ROLE] = "WAITER" // Скидаємо на дефолт
        }
    }
}