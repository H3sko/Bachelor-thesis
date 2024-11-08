package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ExposedUsers(
    val username: String,
    val passwordHash: String,
    val salt: ByteArray
)

// TODO: OnlineUsers
@Serializable
data class ExposedOnlineUser(
    val userId: Int,
    val token: String
)

@Serializable
data class ExposedDevices(
    val userId: Int,
    val name: String,
    val serialNumber: String
)

@Serializable
data class ExposedLocations(
    val deviceId: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)

@Serializable
data class ExposedGeofences(
    val deviceId: Int
)

@Serializable
data class ExposedGeofenceVertices(
    val geofenceId: Int,
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class PasswordChangeRequest(
    val username: String,
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class ExposedDeviceResponse(
    val id: Int,
    val userId: Int,
    val name: String,
    val serialNumber: String
)

@Serializable
data class UserCredentialsRequest(
    val username: String,
    val passwordHash: String
)

@Serializable
data class UserRequest(
    val username: String,
    val password: String
)

@Serializable
data class DeviceCredentials(
    val name: String,
    val owner: String
)

@Serializable
data class GeofenceVertexRequest(
    val geofenceId: Int
)

@Serializable
data class GeofenceVertex(
    val latitude: Double,
    val longitude: Double
)

@Serializable
data class GeofenceResponse(
    val geofenceId: Int,
    val vertices: List<GeofenceVertex>
)

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)

@Serializable
data class LocationWithId(
    val id: Int,
    val deviceId: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)

data class GeofenceNotification(
    val title: String,
    val deviceId: Int,
    val deviceName: String
//    val breachLocation: String // e.g., "Lat: xx, Lng: xx"
)