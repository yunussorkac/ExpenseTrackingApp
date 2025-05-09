package com.app.expensetracking.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.booleanDataStore by preferencesDataStore(name = "boolean_data_store")

class BooleanDataStore(private val context: Context) {
    private fun getBooleanKey(key: String) = booleanPreferencesKey(key)

    fun getBoolean(key: String, defaultValue: Boolean = false): Flow<Boolean> {
        return context.booleanDataStore.data.map { preferences ->
            preferences[getBooleanKey(key)] ?: defaultValue
        }
    }

    suspend fun saveBoolean(key: String, value: Boolean) {
        context.booleanDataStore.edit { preferences ->
            preferences[getBooleanKey(key)] = value
        }
    }

    suspend fun clearBoolean(key: String) {
        context.booleanDataStore.edit { preferences ->
            preferences.remove(getBooleanKey(key))
        }
    }
}