package bachelorThesis.app.domain.useCase.users

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.TokenJson
import bachelorThesis.app.data.remote.dto.UserRequest
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(payload: UserRequest): Flow<Resource<TokenJson>> {
        return repository.login(payload)
    }
}