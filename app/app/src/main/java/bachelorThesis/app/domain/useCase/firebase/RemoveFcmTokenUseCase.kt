package bachelorThesis.app.domain.useCase.firebase

import bachelorThesis.app.common.Resource
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveFcmTokenUseCase @Inject constructor(
    private val repository: Repository
)   {
    operator fun invoke(credentials: String): Flow<Resource<String>> {
        return repository.removeFcmToken(credentials)
    }
}