package com.project.labs.nutrigrow.data.local.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.RememberedCredential
import com.project.labs.nutrigrow.data.model.SendOtpModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    private val id = stringPreferencesKey("id")
    private val role = stringPreferencesKey("role")
    private val region = stringPreferencesKey("region")
    private val token = stringPreferencesKey("token")

    private val email = stringPreferencesKey("email")
    private val password = stringPreferencesKey("password")

    private val rememberedEmail = stringPreferencesKey("remembered_email")
    private val rememberedPassword = stringPreferencesKey("remembered_password")
    private val rememberMe = booleanPreferencesKey("remember_me")
    private val reminderEnabled = booleanPreferencesKey("reminder_enabled")

    suspend fun saveAuth(auth: AuthModel) {
        dataStore.edit {
            it[id] = auth.id
            it[role] = auth.role
            it[region] = auth.region
            it[token] = "Bearer ${auth.token}"
        }
    }

    suspend fun saveOTP(auth: SendOtpModel) {
        dataStore.edit {
            it[email] = auth.email
            it[password] = auth.password
        }
    }

    fun getAuth(): Flow<AuthModel> = dataStore.data.map {
        AuthModel(
            it[id] ?: "",
            it[role] ?: "",
            it[region] ?: "",
            it[token] ?: ""
        )
    }

    fun getOTP(): Flow<SendOtpModel> = dataStore.data.map {
        SendOtpModel(
            it[email] ?: "",
            it[password] ?: "",
        )
    }

    suspend fun saveRememberedCredential(emailValue: String, passwordValue: String) {
        dataStore.edit {
            it[rememberedEmail] = emailValue
            it[rememberedPassword] = passwordValue
            it[rememberMe] = true
        }
    }

    suspend fun clearRememberedCredential() {
        dataStore.edit {
            it.remove(rememberedEmail)
            it.remove(rememberedPassword)
            it[rememberMe] = false
        }
    }

    fun getRememberedCredential(): Flow<RememberedCredential> = dataStore.data.map {
        RememberedCredential(
            email = it[rememberedEmail] ?: "",
            password = it[rememberedPassword] ?: "",
            remember = it[rememberMe] ?: false,
        )
    }

    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[reminderEnabled] = enabled }
    }

    fun getReminderEnabled(): Flow<Boolean> = dataStore.data.map { it[reminderEnabled] ?: true }

    suspend fun destroyUser() = dataStore.edit {
        val keptEmail = it[rememberedEmail]
        val keptPassword = it[rememberedPassword]
        val keptFlag = it[rememberMe] ?: false
        val keptReminder = it[reminderEnabled]

        it.clear()

        if (keptFlag && keptEmail != null && keptPassword != null) {
            it[rememberedEmail] = keptEmail
            it[rememberedPassword] = keptPassword
            it[rememberMe] = true
        }
        if (keptReminder != null) {
            it[reminderEnabled] = keptReminder
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}