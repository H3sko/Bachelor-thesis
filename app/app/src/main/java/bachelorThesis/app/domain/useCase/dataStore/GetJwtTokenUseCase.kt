package bachelorThesis.app.domain.useCase.dataStore

import bachelorThesis.app.domain.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetJwtTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
)  {
    operator fun invoke(): Flow<String> {
        return dataStoreRepository.getJwtToken()
    }
}