package bachelorThesis.app.domain.useCase.geofences

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddGeofenceUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String, vertices: List<GeofenceVertex>): Flow<Resource<String>> {
        return repository.addGeofence(credentials, deviceId, vertices)
    }
}