package bachelorThesis.app.domain.useCase.locations

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.dto.LocationDto
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val repository: Repository
)  {
    operator fun invoke(credentials: String, deviceId: String): Flow<Resource<LocationDto>> {
        return repository.getLocation(credentials, deviceId)
    }
}