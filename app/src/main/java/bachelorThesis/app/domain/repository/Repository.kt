package bachelorThesis.app.domain.repository

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.remote.dto.Device
import bachelorThesis.app.data.remote.dto.DeviceCredentials
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.data.remote.dto.LocationDto
import bachelorThesis.app.data.remote.dto.TokenJson
import bachelorThesis.app.data.remote.dto.UserRequest
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun register(payload: UserRequest): Flow<Resource<Int>>

    fun login(payload: UserRequest): Flow<Resource<TokenJson>>

    fun getDevices(credentials: String): Flow<Resource<List<Device>>>

    fun addDevice(credentials: String, payload: DeviceCredentials): Flow<Resource<Int>>

    fun removeDevice(credentials: String, deviceId: String): Flow<Resource<Boolean>>

    fun getLocation(credentials: String, deviceId: String): Flow<Resource<LocationDto>>

    fun getAllLocations(credentials: String, deviceId: String, limit: Int): Flow<Resource<List<LocationDto>>>

    fun getGeofence(credentials: String, deviceId: String): Flow<Resource<List<GeofenceVertex>>>

    fun addGeofence(credentials: String, deviceId: String, vertices: List<GeofenceVertex>): Flow<Resource<String>>

    fun removeGeofence(credentials: String, deviceId: String): Flow<Resource<String>>

    fun addFcmToken(credentials: String, token: String, activeNotification: Boolean): Flow<Resource<String>>

    fun removeFcmToken(credentials: String): Flow<Resource<String>>

    fun putNotificationStatus(credentials: String, newValue: Boolean): Flow<Resource<Boolean>>

    fun getNotificationStatus(credentials: String): Flow<Resource<Boolean>>
}