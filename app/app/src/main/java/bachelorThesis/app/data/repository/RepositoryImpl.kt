package bachelorThesis.app.data.repository

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.dto.Device
import bachelorThesis.app.data.model.dto.LocationDto
import bachelorThesis.app.data.model.dto.TokenDto
import bachelorThesis.app.data.model.dto.toDevice
import bachelorThesis.app.data.model.json.DeviceCredentialsJson
import bachelorThesis.app.data.model.json.DeviceJson
import bachelorThesis.app.data.model.json.FcmTokenJson
import bachelorThesis.app.data.model.json.NotificationSwitchJson
import bachelorThesis.app.data.model.json.UserJson
import bachelorThesis.app.data.remote.BackendApi
import bachelorThesis.app.domain.repository.Repository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: BackendApi
) : Repository {
    override fun register(payload: UserJson): Flow<Resource<Int>> {
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

    override fun login(payload: UserJson): Flow<Resource<TokenDto>> {
        return flow {
            try {
                emit(Resource.Loading<TokenDto>())
                val token = api.login(UserJson(username = payload.username, password = payload.password))
                emit(Resource.Success<TokenDto>(token))
            } catch (e: HttpException) {
                emit(Resource.Error<TokenDto>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<TokenDto>(-1))
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

    override fun addDevice(credentials: String, payload: DeviceCredentialsJson): Flow<Resource<Int>> {
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

    override fun getGeofence(credentials: String, deviceId: String): Flow<Resource<List<LatLng>>> {
        return flow {
            try {
                emit(Resource.Loading<List<LatLng>>())
                val vertices = api.getGeofence(credentials, deviceId)
                emit(Resource.Success<List<LatLng>>(vertices))
            } catch (e: HttpException) {
                emit(Resource.Error<List<LatLng>>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<List<LatLng>>(-1))
            }
        }
    }

    override fun addGeofence(credentials: String, deviceId: String, vertices: List<LatLng>): Flow<Resource<String>> {
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
                val deleted = api.removeGeofence(credentials, deviceId)
                emit(Resource.Success<String>(deleted.message))
            } catch (e: HttpException) {
                emit(Resource.Error<String>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<String>(-1))
            }
        }
    }

    override fun addFcmToken(credentials: String, token: String, activeNotification: Boolean): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading<String>())
                val added = api.addFcmToken(credentials, FcmTokenJson(token, activeNotification))
                emit(Resource.Success<String>(added.message))
            } catch (e: HttpException) {
                emit(Resource.Error<String>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<String>(-1))
            }
        }
    }

    override fun removeFcmToken(credentials: String): Flow<Resource<String>> {
        return flow {
            try {
                emit(Resource.Loading<String>())
                val deleted = api.removeFcmToken(credentials)
                emit(Resource.Success<String>(deleted.message))
            } catch (e: HttpException) {
                emit(Resource.Error<String>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<String>(-1))
            }
        }
    }

    override fun putNotificationStatus(credentials: String, newValue: Boolean): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading<Boolean>())
                val updated = api.putNotificationStatus(credentials, NotificationSwitchJson(newValue))
                emit(Resource.Success<Boolean>(updated))
            } catch (e: HttpException) {
                emit(Resource.Error<Boolean>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<Boolean>(-1))
            }
        }
    }

    override fun getNotificationStatus(credentials: String): Flow<Resource<Boolean>> {
        return flow {
            try {
                emit(Resource.Loading<Boolean>())
                val notificationStatus = api.getNotificationStatus(credentials)
                emit(Resource.Success<Boolean>(notificationStatus))
            } catch (e: HttpException) {
                emit(Resource.Error<Boolean>(e.code()))
            }
            catch (e: IOException) {
                emit(Resource.Error<Boolean>(-1))
            }
        }
    }
}