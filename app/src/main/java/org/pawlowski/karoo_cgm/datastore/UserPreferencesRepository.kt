package org.pawlowski.karoo_cgm.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val email: String?,
    val password: String?,
    val authToken: String?,
    val tokenExpiration: Long?,
    val patientId: String?,
    val accountId: String?
)

class UserPreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferencesKeys {
        val EMAIL = stringPreferencesKey("email")
        val PASSWORD = stringPreferencesKey("password")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val ACCOUNT_ID = stringPreferencesKey("account_id")
        val TOKEN_EXPIRATION = stringPreferencesKey("token_expiration")
        val PATIENT_ID = stringPreferencesKey("patient_id")
    }

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            val email = preferences[PreferencesKeys.EMAIL]
            val password = preferences[PreferencesKeys.PASSWORD]
            val authToken = preferences[PreferencesKeys.AUTH_TOKEN]
            val tokenExpiration = preferences[PreferencesKeys.TOKEN_EXPIRATION]?.toLongOrNull()
            val patientId = preferences[PreferencesKeys.PATIENT_ID]
            val accountId = preferences[PreferencesKeys.ACCOUNT_ID]
            UserPreferences(email, password, authToken, tokenExpiration, patientId, accountId)
        }

    suspend fun updateEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.EMAIL] = email
        }
    }

    suspend fun updatePassword(password: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PASSWORD] = password
        }
    }

    suspend fun updateAuthToken(token: String, expiration: Long, accountId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.AUTH_TOKEN] = token
            preferences[PreferencesKeys.TOKEN_EXPIRATION] = expiration.toString()
            preferences[PreferencesKeys.ACCOUNT_ID] = accountId
        }
    }

    suspend fun updatePatientId(patientId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PATIENT_ID] = patientId
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
