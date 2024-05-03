package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class ExposedUsers(
    val username: String,
    val password: String,
)

@Serializable
data class ExposedDevices(
    val userId: Int,
    val name: String
)

@Serializable
data class ExposedLocations(
    val deviceId: Int,
    val latitude: String,
    val longitude: String,
    val timestamp: String
)

@Serializable
data class ExposedGeofences(
    val deviceId: Int
)

@Serializable
data class ExposedGeofenceVertices(
    val geofenceId: Int,
    val latitude: String,
    val longitude: String
)

@Serializable
data class PasswordChangeRequest(
    val username: String,
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class DeviceNameRequest(val name: String)

@Serializable
data class DeviceLocationRequest(val deviceId: Int)

@Serializable
data class GeofenceVertexRequest(val geofenceId: Int)
   