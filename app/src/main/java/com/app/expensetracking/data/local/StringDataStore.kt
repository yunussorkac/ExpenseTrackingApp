package com.app.expensetracking.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.stringDataStore by preferencesDataStore(name = "string_data_store")

class StringDataStore(private val context: Context) {
    private fun getStringKey(key: String) = stringPreferencesKey(key)

    fun getString(key: String, defaultValue: String = ""): Flow<String> {
        return context.stringDataStore.data.map { preferences ->
            preferences[getStringKey(key)] ?: defaultValue
        }
    }

    suspend fun saveString(key: String, value: String) {
        context.stringDataStore.edit { preferences ->
            preferences[getStringKey(key)] = value
        }
    }

    suspend fun clearString(key: String) {
        context.stringDataStore.edit { preferences ->
            preferences.remove(getStringKey(key))
        }
    }
}
