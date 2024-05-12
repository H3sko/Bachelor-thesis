package bachelorThesis.app.domain.useCase.geofences

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGeofenceUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String): Flow<Resource<List<GeofenceVertex>>> {
        return repository.getGeofence(credentials, deviceId)
    }
}