package bachelorThesis.app.ui.map

import bachelorThesis.app.data.remote.dto.Device
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.data.remote.dto.LocationDto
import com.google.maps.android.compose.CameraPositionState

data class MapScreenState(
    val error: String? = null,
    val token: String = "",
    val devices: List<Device> = emptyList(),
    val device: Device? = null,
    val deviceGeofenceVertices: List<GeofenceVertex> = emptyList(),
    val locationLatest: LocationDto? = null,
    val locationHistory: List<LocationDto> = emptyList(),
    val cameraPositionState: CameraPositionState = CameraPositionState(),
    // Requests:
    // addDevice()
    val deviceName: String = "",
    val deviceOwner: String = "",
    //  addGeofence()
    val addVertices: List<GeofenceVertex> = emptyList(),
)
