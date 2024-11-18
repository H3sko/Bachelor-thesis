package bachelorThesis.app.ui.map

import bachelorThesis.app.data.model.dto.Device
import bachelorThesis.app.data.model.dto.LocationDto
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState

data class MapScreenState(
    val error: String? = null,
    val message: String? = null,
    val token: String = "",
    val devices: List<Device> = emptyList(),
    // Current device stuff
    val device: Device? = null,
    val deviceGeofenceVertices: List<LatLng> = emptyList(),
    var locationLatest: LocationDto? = null,
    val locationHistory: List<LocationDto> = emptyList(),
    // Center, History & Geofence
    val cameraPositionState: CameraPositionState = CameraPositionState(),
    var showLocationHistory: Boolean = false,
    var showGeofence: Boolean = false,
    // Creating geofence
    var addedGeofenceVertices: List<LatLng> = emptyList(),
    var addingGeofence: Boolean = false,
    // Geofence notifications
    var geofenceNotificationStatus: Boolean = false
)
