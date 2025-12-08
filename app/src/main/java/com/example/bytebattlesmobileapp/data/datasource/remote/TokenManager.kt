package com.example.bytebattlesmobileapp.data.datasource.remote

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import android.util.Base64
import org.json.JSONObject
import java.nio.charset.StandardCharsets

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class TokenManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
    }

    suspend fun saveToken(accessToken: String, refreshToken: String? = null) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            refreshToken?.let { preferences[REFRESH_TOKEN_KEY] = it }

            // Parse JWT to get expiration and user ID
            val claims = parseJwt(accessToken)

            // Get user ID (sub claim)
            val userId = claims.optString("sub")
            if (userId.isNotEmpty()) {
                preferences[USER_ID_KEY] = userId
            }

            // Get expiration (exp claim)
            val exp = claims.optLong("exp")
            if (exp > 0) {
                preferences[EXPIRES_AT_KEY] = exp
            }
        }
    }

    suspend fun getTokens(): Pair<String, String>? {
        val preferences = dataStore.data.first()
        val accessToken = preferences[ACCESS_TOKEN_KEY]
        val refreshToken = preferences[REFRESH_TOKEN_KEY]

        return if (accessToken != null && refreshToken != null) {
            Pair(accessToken, refreshToken)
        } else {
            null
        }
    }

    fun getAccessToken(): Flow<String?> = dataStore.data
        .map { it[ACCESS_TOKEN_KEY] }

    fun getCurrentUserId(): String? = runBlocking {
        dataStore.data.first()[USER_ID_KEY]
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun parseJwt(token: String): JSONObject {
        return try {
            // Split the JWT into its three parts
            val parts = token.split(".")
            if (parts.size != 3) {
                return JSONObject()
            }

            // Decode the payload (second part)
            val payload = parts[1]

            // Android Base64 decode
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val decodedString = String(decodedBytes, StandardCharsets.UTF_8)

            // Parse as JSON
            JSONObject(decodedString)
        } catch (e: Exception) {
            // Log error for debugging
            android.util.Log.e("TokenManager", "Failed to parse JWT: ${e.message}")
            JSONObject()
        }
    }

    // Helper method to check if token is expired
    suspend fun isTokenExpired(): Boolean = runBlocking {
        val preferences = dataStore.data.first()
        val expiresAt = preferences[EXPIRES_AT_KEY] ?: return@runBlocking true

        // exp is in seconds, convert to milliseconds
        val expirationTime = expiresAt * 1000L
        val currentTime = System.currentTimeMillis()

        return@runBlocking currentTime >= expirationTime
    }

    // Helper method to get token expiration time
    suspend fun getTokenExpiration(): Long? = runBlocking {
        val preferences = dataStore.data.first()
        preferences[EXPIRES_AT_KEY]
    }
}