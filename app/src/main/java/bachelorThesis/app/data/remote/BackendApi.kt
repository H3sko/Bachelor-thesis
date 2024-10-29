package bachelorThesis.app.data.remote

import bachelorThesis.app.data.remote.dto.DeviceDto
import bachelorThesis.app.data.remote.dto.DeviceJson
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.data.remote.dto.LocationDto
import bachelorThesis.app.data.remote.dto.Response
import bachelorThesis.app.data.remote.dto.TokenJson
import bachelorThesis.app.data.remote.dto.UserJson
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface BackendApi {

    @Headers("Content-Type: application/json")
    @POST("/user/register")
    suspend fun register(@Body payload: UserJson): Int

    @Headers("Content-Type: application/json")
    @POST("user/login")
    suspend fun login(@Body payload: UserJson): TokenJson

    @GET("device/getUserDevices")
    suspend fun getDevices(@Header("Authorization") credentials: String): List<DeviceDto>

    @Headers("Content-Type: application/json")
    @POST("device/add")
    suspend fun addDevice(@Header("Authorization") credentials: String, @Body payload: DeviceJson): Int

    @DELETE("device/remove/{deviceId}")
    suspend fun removeDevice(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): Boolean

    @GET("location/getLatest/{deviceId}")
    suspend fun getLocation(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): LocationDto

    @GET("location/getAll/{deviceId}")
    suspend fun getAllLocations(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String, @Query("limit") limit: Int): List<LocationDto>

    @GET("geofence/device/{deviceId}")
    suspend fun getGeofence(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): List<GeofenceVertex>

    @Headers("Content-Type: application/json")
    @POST("geofence/add/{deviceId}")
    suspend fun addGeofence(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String, @Body vertices: List<GeofenceVertex>): Response

    @DELETE("geofence/delete/device/{deviceId}")
    suspend fun removeGeofence(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): Response
}