package bachelorThesis.app.domain.useCase.users

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.dto.TokenDto
import bachelorThesis.app.data.model.json.UserJson
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(payload: UserJson): Flow<Resource<TokenDto>> {
        return repository.login(payload)
    }
}