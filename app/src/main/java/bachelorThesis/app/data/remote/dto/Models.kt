package bachelorThesis.app.data.remote.dto

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Keep
data class DeviceDto(
    val id: Int,
    val userId: Int,
    val name: String,
    val serialNumber: String
)

@Parcelize
data class Device(
    val id: Int,
    val name: String
) : Parcelable

fun DeviceDto.toDevice() : Device {
    return Device(
        id = id,
        name = name
    )
}

@Keep
data class DeviceCredentials(val name: String, val owner: String)

@Keep
data class Paging(
    val next: String
)

@Keep
data class LocationsResponse(
    val data: List<LocationDto>,
    val paging: Paging
)

@Keep
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)

@Keep
data class GeofenceVertex(
    val latitude: Double,
    val longitude: Double
)

@Keep
data class GeofenceDto(
    val geofenceId: Int,
    val vertices: List<GeofenceVertex>
)

@Keep
data class UserRequest(
    val username: String,
    val password: String
)

@Keep
data class UserJson(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)

@Keep
data class DeviceJson(
    @SerializedName("name") val name: String?,
    @SerializedName("owner") val owner: String?
)

@Keep
data class GeofenceJson(
    val vertices: List<GeofenceVertex>?
)

@Keep
data class TokenJson(
    @SerializedName("token") val token: String?
)

