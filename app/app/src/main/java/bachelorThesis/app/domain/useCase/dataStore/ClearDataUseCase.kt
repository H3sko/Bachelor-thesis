package bachelorThesis.app.domain.useCase.dataStore

import bachelorThesis.app.domain.repository.DataStoreRepository
import javax.inject.Inject

class ClearDataUseCase @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
)  {
    suspend operator fun invoke() {
        return dataStoreRepository.deleteData()
    }
}