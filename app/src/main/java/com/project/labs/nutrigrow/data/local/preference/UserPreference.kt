package com.project.labs.nutrigrow.data.local.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.labs.nutrigrow.data.model.AuthModel
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

    suspend fun destroyUser() = dataStore.edit { it.clear() }

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