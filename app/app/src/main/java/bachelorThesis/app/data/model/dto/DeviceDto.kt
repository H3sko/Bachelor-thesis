package bachelorThesis.app.data.model.dto

import android.os.Parcelable
import androidx.annotation.Keep
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