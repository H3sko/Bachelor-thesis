package bachelorThesis.app.domain.useCase.geofences

import bachelorThesis.app.common.Resource
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveGeofenceUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String): Flow<Resource<String>> {
        return repository.removeGeofence(credentials, deviceId)
    }
}