package com.playaxis.currencyapp.core.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import com.playaxis.currencyapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class DatastoreManager(context: Context) {

    private val datastore: DataStore<Preferences> = PreferenceDataStoreFactory.create {
        context.applicationContext.preferencesDataStoreFile(Constants.DATA_STORE_NAME)
    }

    suspend fun saveData(key: String, value: Any?) {
        datastore.edit { preferences ->
            when (value) {
                is String -> {
                    preferences[stringPreferencesKey(key)] = value
                    Log.e(Constants.TAG, "String saved:$value")
                }

                is Int -> {
                    preferences[intPreferencesKey(key)] = value
                    Log.e(Constants.TAG, "Int saved:$value")
                }

                is Long -> {
                    preferences[longPreferencesKey(key)] = value
                    Log.e(Constants.TAG, "Long saved:$value")
                }

                is Float -> {
                    preferences[floatPreferencesKey(key)] = value
                    Log.e(Constants.TAG, "Float saved:$value")
                }

                is Boolean -> {
                    preferences[booleanPreferencesKey(key)] = value
                    Log.e(Constants.TAG, "Boolean saved:$value")
                }

                else -> throw IllegalArgumentException("Unsupported preference type")
            }
        }
    }

    suspend fun <T> getData(key: String, defaultValue: T): Flow<T> = flow {
        val preferencesKey = when (defaultValue) {
            is String -> stringPreferencesKey(key)
            is Int -> intPreferencesKey(key)
            is Long -> longPreferencesKey(key)
            is Float -> floatPreferencesKey(key)
            is Boolean -> booleanPreferencesKey(key)
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
        val preferences = datastore.data.first()
        val value = preferences[preferencesKey] ?: defaultValue
        emit(value as T)
    }.catch { exception ->
        if (exception is IOException) {
            emit(defaultValue)
        } else {
            throw exception
        }

    }

    suspend fun remove(key: String) {
        datastore.edit { preference ->
            preference.remove(stringPreferencesKey(key))
            preference.remove(intPreferencesKey(key))
            preference.remove(longPreferencesKey(key))
            preference.remove(floatPreferencesKey(key))
            preference.remove(booleanPreferencesKey(key))
        }
    }

    suspend fun clearData() {
        datastore.edit { preferences ->
            preferences.clear()
        }
    }

}