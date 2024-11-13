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
    // Current device stuff
    val device: Device? = null,
    val deviceGeofenceVertices: List<GeofenceVertex> = emptyList(),
    var locationLatest: LocationDto? = null,
    val locationHistory: List<LocationDto> = emptyList(),
    // Center, History & Geofence
    val cameraPositionState: CameraPositionState = CameraPositionState(),
    var showLocationHistory: Boolean = false,
    var showGeofence: Boolean = false,
    // Creating geofence
    var addedGeofenceVertices: List<GeofenceVertex> = emptyList(),
    var addingGeofence: Boolean = false,
    // Geofence notifications
    var geofenceNotificationStatus: Boolean = false
)
