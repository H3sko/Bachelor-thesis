package bachelorThesis.app.ui.map

import bachelorThesis.app.data.remote.dto.Device
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.data.remote.dto.LocationDto
import com.google.maps.android.compose.CameraPositionState

data class MapScreenState(
    val error: String? = null,
    val message: String? = null,
    val token: String = "",
    val devices: List<Device> = emptyList(),
    val cameraPositionState: CameraPositionState = CameraPositionState(), // TODO: s tymto zatial neviem co robitß
    // Current device stuff
    val device: Device? = null,
    val deviceGeofenceVertices: List<GeofenceVertex> = emptyList(),
    val locationLatest: LocationDto? = null,
    val locationHistory: List<LocationDto> = emptyList(),
    // Requests:
    //  addGeofence()
    val addVertices: List<GeofenceVertex> = emptyList() // TODO: toto asi vymazat
)
