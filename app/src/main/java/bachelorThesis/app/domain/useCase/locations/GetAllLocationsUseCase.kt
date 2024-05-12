package bachelorThesis.app.domain.useCase.locations

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.LocationDto
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllLocationsUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String): Flow<Resource<List<LocationDto>>> {
        return repository.getAllLocations(credentials, deviceId, 10)
    }
}