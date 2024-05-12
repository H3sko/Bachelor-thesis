package bachelorThesis.app.domain.useCase.users

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.UserRequest
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(payload: UserRequest): Flow<Resource<Int>> {
        return repository.register(payload)
    }
}