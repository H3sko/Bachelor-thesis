package bachelorThesis.app.domain.useCase.geofences

import bachelorThesis.app.common.Resource
import bachelorThesis.app.domain.repository.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGeofenceUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String): Flow<Resource<List<LatLng>>> {
        return repository.getGeofence(credentials, deviceId)
    }
}