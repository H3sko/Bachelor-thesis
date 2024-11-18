package bachelorThesis.app.domain.useCase.devices

import bachelorThesis.app.common.Resource
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveDeviceUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String): Flow<Resource<Boolean>> {
        return repository.removeDevice(credentials, deviceId)
    }
}