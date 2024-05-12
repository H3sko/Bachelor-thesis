package bachelorThesis.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    fun getJwtToken(): Flow<String>

    suspend fun setJwtToken(tokenValue: String)

    suspend fun deleteData()
}