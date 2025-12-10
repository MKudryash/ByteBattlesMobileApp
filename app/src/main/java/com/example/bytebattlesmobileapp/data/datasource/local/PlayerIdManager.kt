package com.example.bytebattlesmobileapp.data.datasource.local

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerIdManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "battle_prefs"
        private const val KEY_PLAYER_ID = "player_id"
        private const val KEY_SESSION_ID = "session_id"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Сохраняем playerId
    fun savePlayerId(playerId: String) {
        prefs.edit().putString(KEY_PLAYER_ID, playerId).apply()
    }

    // Получаем playerId
    fun getPlayerId(): String? = prefs.getString(KEY_PLAYER_ID, null)

    // Проверяем, есть ли сохраненный playerId
    fun hasPlayerId(): Boolean = prefs.contains(KEY_PLAYER_ID)

    // Очищаем playerId (при выходе из аккаунта)
    fun clearPlayerId() {
        prefs.edit().remove(KEY_PLAYER_ID).apply()
    }

    // Сохраняем session ID (если нужно)
    fun saveSessionId(sessionId: String) {
        prefs.edit().putString(KEY_SESSION_ID, sessionId).apply()
    }

    // Получаем session ID
    fun getSessionId(): String? = prefs.getString(KEY_SESSION_ID, null)

    // Очищаем все данные сессии
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}