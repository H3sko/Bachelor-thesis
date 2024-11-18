package bachelorThesis.app.domain.useCase.dataStore

import bachelorThesis.app.domain.repository.DataStoreRepository
import javax.inject.Inject

class SetJwtTokenUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
)  {
    suspend operator fun invoke(tokenValue: String) {
        return dataStoreRepository.setJwtToken(tokenValue)
    }
}