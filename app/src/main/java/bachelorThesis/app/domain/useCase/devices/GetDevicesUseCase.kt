package bachelorThesis.app.domain.useCase.devices

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.dto.Device
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDevicesUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(credentials: String): Flow<Resource<List<Device>>> {
        return repository.getDevices(credentials)
    }
}