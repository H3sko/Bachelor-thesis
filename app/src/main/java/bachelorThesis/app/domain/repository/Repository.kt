package bachelorThesis.app.domain.repository

import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.dto.Device
import bachelorThesis.app.data.model.dto.LocationDto
import bachelorThesis.app.data.model.dto.TokenDto
import bachelorThesis.app.data.model.json.DeviceCredentialsJson
import bachelorThesis.app.data.model.json.UserJson
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun register(payload: UserJson): Flow<Resource<Int>>

    fun login(payload: UserJson): Flow<Resource<TokenDto>>

    fun getDevices(credentials: String): Flow<Resource<List<Device>>>

    fun addDevice(credentials: String, payload: DeviceCredentialsJson): Flow<Resource<Int>>

    fun removeDevice(credentials: String, deviceId: String): Flow<Resource<Boolean>>

    fun getLocation(credentials: String, deviceId: String): Flow<Resource<LocationDto>>

    fun getAllLocations(credentials: String, deviceId: String, limit: Int): Flow<Resource<List<LocationDto>>>

    fun getGeofence(credentials: String, deviceId: String): Flow<Resource<List<LatLng>>>

    fun addGeofence(credentials: String, deviceId: String, vertices: List<LatLng>): Flow<Resource<String>>

    fun removeGeofence(credentials: String, deviceId: String): Flow<Resource<String>>

    fun addFcmToken(credentials: String, token: String, activeNotification: Boolean): Flow<Resource<String>>

    fun removeFcmToken(credentials: String): Flow<Resource<String>>

    fun putNotificationStatus(credentials: String, newValue: Boolean): Flow<Resource<Boolean>>

    fun getNotificationStatus(credentials: String): Flow<Resource<Boolean>>
}