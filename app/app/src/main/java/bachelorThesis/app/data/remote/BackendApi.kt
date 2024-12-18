package bachelorThesis.app.data.remote

import bachelorThesis.app.data.model.dto.DeviceDto
import bachelorThesis.app.data.model.dto.LocationDto
import bachelorThesis.app.data.model.dto.MessageDto
import bachelorThesis.app.data.model.dto.TokenDto
import bachelorThesis.app.data.model.json.DeviceJson
import bachelorThesis.app.data.model.json.FcmTokenJson
import bachelorThesis.app.data.model.json.NotificationSwitchJson
import bachelorThesis.app.data.model.json.UserJson
import com.google.android.gms.maps.model.LatLng
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface BackendApi {

    @Headers("Content-Type: application/json")
    @POST("/user/register")
    suspend fun register(@Body payload: UserJson): Int

    @Headers("Content-Type: application/json")
    @POST("user/login")
    suspend fun login(@Body payload: UserJson): TokenDto

    @GET("device/all")
    suspend fun getDevices(@Header("Authorization") credentials: String): List<DeviceDto>

    @Headers("Content-Type: application/json")
    @POST("device")
    suspend fun addDevice(@Header("Authorization") credentials: String, @Body payload: DeviceJson): Int

    @DELETE("device/{deviceId}")
    suspend fun removeDevice(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): Boolean

    @GET("location/latest/device/{deviceId}")
    suspend fun getLocation(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): LocationDto

    @GET("location/all/device/{deviceId}")
    suspend fun getAllLocations(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String, @Query("limit") limit: Int): List<LocationDto>

    @GET("geofence/device/{deviceId}")
    suspend fun getGeofence(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): List<LatLng>

    @Headers("Content-Type: application/json")
    @POST("geofence/device/{deviceId}")
    suspend fun addGeofence(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String, @Body vertices: List<LatLng>): MessageDto

    @DELETE("geofence/device/{deviceId}")
    suspend fun removeGeofence(@Header("Authorization") credentials: String, @Path("deviceId") deviceId: String): MessageDto

    @POST("online-user")
    suspend fun addFcmToken(@Header("Authorization") credentials: String, @Body payload: FcmTokenJson): MessageDto

    @DELETE("online-user")
    suspend fun removeFcmToken(@Header("Authorization") credentials: String): MessageDto

    @PUT("online-user/notification/switch")
    suspend fun putNotificationStatus(@Header("Authorization") credentials: String, @Body payload: NotificationSwitchJson): Boolean

    @GET("online-user/notification/status")
    suspend fun getNotificationStatus(@Header("Authorization") credentials: String): Boolean

}