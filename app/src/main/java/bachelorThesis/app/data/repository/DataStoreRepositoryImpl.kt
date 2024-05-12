package bachelorThesis.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import bachelorThesis.app.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): DataStoreRepository
{
    private val token = stringPreferencesKey("jwtToken")
    override fun getJwtToken(): Flow<String> =
        dataStore.data
            .catch{ exception ->
                if (exception is IOException) { emit(emptyPreferences()) }
                else { throw exception }
            }
            .map { it[token].orEmpty() }


    override suspend fun setJwtToken(tokenValue: String) {
        dataStore.edit {
            it[token] = tokenValue
        }
    }

    override suspend fun deleteData() {
        dataStore.edit { it.clear() }
    }
}