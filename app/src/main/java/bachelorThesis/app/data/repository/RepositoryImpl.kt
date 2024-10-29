package bachelorThesis.app.data.repository

import android.util.Log
import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.BackendApi
import bachelorThesis.app.data.remote.dto.Device
import bachelorThesis.app.data.remote.dto.DeviceCredentials
import bachelorThesis.app.data.remote.dto.DeviceJson
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.data.remote.dto.LocationDto
import bachelorThesis.app.data.remote.dto.TokenJson
import bachelorThesis.app.data.remote.dto.UserJson
import bachelorThesis.app.data.remote.dto.UserRequest
import bachelorThesis.app.data.remote.dto.toDevice
import bachelorThesis.app.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: BackendApi
) : Repository {
    override fun register(payload: UserRequest): Flow<Resource<Int>> {
        return flow {
            try {
                emit(Resource.Loading<Int>())
                val userId = api.register(UserJson(username = payload.username, password = payload.password))
                emit(Resource.Success<Int>(userId))
            } catch (e: HttpException) {
                emit(Resource.Error<Int>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<Int>(-1))
            }
        }
    }

    override fun login(payload: UserRequest): Flow<Resource<TokenJson>> {
        return flow {
            try {
                emit(Resource.Loading<TokenJson>())
                val token = api.login(UserJson(username = payload.username, password = payload.password))
                emit(Resource.Success<TokenJson>(token))
            } catch (e: HttpException) {
                emit(Resource.Error<TokenJson>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<TokenJson>(-1))
            }
        }
    }

    override fun getDevices(credentials: String): Flow<Resource<List<Device>>> {
        return flow {
            try {
                emit(Resource.Loading<List<Device>>())
                val devices = api.getDevices(credentials).map { it.toDevice() }
                emit(Resource.Success<List<Device>>(devices))
            } catch (e: HttpException) {
                emit(Resource.Error<List<Device>>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<List<Device>>(-1))
            }
        }
    }

    override fun addDevice(credentials: String, payload: DeviceCredentials): Flow<Resource<Int>> {
        return flow {
            try {
                emit(Resource.Loading<Int>())
                val deviceId = api.addDevice(credentials, DeviceJson(name = payload.name, owner = payload.owner))
                emit(Resource.Success<Int>(deviceId))
            } catch (e: HttpException) {
                emit(Resource.Error<Int>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<Int>(-1))
            }
        }
    }

    override fun removeDevice(credentials: String, deviceId: String): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading<Boolean>())
                val deleted = api.removeDevice(credentials, deviceId)
                emit(Resource.Success<Boolean>(deleted))
            } catch (e: HttpException) {
                emit(Resource.Error<Boolean>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<Boolean>(-1))
            }
        }
    }

    override fun getLocation(credentials: String, deviceId: String): Flow<Resource<LocationDto>> {
        return flow {
            try {
                emit(Resource.Loading<LocationDto>())
                val location = api.getLocation(credentials, deviceId)
                emit(Resource.Success<LocationDto>(location))
            } catch (e: HttpException) {
                emit(Resource.Error<LocationDto>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<LocationDto>(-1))
            }
        }
    }

    override fun getAllLocations(
        credentials: String,
        deviceId: String,
        limit: Int
    ): Flow<Resource<List<LocationDto>>> {
        return flow {
            try {
                emit(Resource.Loading<List<LocationDto>>())
                val locations = api.getAllLocations(credentials, deviceId, limit)
                emit(Resource.Success<List<LocationDto>>(locations))
            } catch (e: HttpException) {
                emit(Resource.Error<List<LocationDto>>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<List<LocationDto>>(-1))
            }
        }
    }

    override fun getGeofence(credentials: String, deviceId: String): Flow<Resource<List<GeofenceVertex>>> {
        return flow {
            try {
                emit(Resource.Loading<List<GeofenceVertex>>())
                val vertices = api.getGeofence(credentials, deviceId)
                emit(Resource.Success<List<GeofenceVertex>>(vertices))
            } catch (e: HttpException) {
                emit(Resource.Error<List<GeofenceVertex>>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<List<GeofenceVertex>>(-1))
            }
        }
    }

    override fun addGeofence(credentials: String, deviceId: String, vertices: List<GeofenceVertex>): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading<String>())
                val created = api.addGeofence(credentials, deviceId, vertices)
                emit(Resource.Success<String>(created.message))
            } catch (e: HttpException) {
                emit(Resource.Error<String>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<String>(-1))
            }
        }
    }

    override fun removeGeofence(credentials: String, deviceId: String): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading<String>())
                Log.d("val deleted = api.removeGeofence(credentials, deviceId)", "pred")
                val deleted = api.removeGeofence(credentials, deviceId)
                Log.d("val deleted = api.removeGeofence(credentials, deviceId)", "po")
                emit(Resource.Success<String>(deleted.message))
            } catch (e: HttpException) {
                emit(Resource.Error<String>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<String>(-1))
            }
        }
    }

}