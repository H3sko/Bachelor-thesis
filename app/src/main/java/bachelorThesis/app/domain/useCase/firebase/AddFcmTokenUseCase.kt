package bachelorThesis.app.domain.useCase.firebase

import bachelorThesis.app.common.Resource
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFcmTokenUseCase @Inject constructor(
    private val repository: Repository
)   {
    operator fun invoke(credentials: String, token: String, activeNotification: Boolean): Flow<Resource<String>> {
        return repository.addFcmToken(credentials, token, activeNotification)
    }
}