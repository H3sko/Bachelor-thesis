package bachelorThesis.app.domain.useCase.devices

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.DeviceCredentials
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddDeviceUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, payload: DeviceCredentials): Flow<Resource<Int>> {
        return repository.addDevice(credentials, payload)
    }
}