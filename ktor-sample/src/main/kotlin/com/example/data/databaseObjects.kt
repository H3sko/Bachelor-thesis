package com.example.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


@Serializable
data class ExposedUsers(
    val loginEmail: String,
    val hashedPassword: String,
)

object Users : IntIdTable("users") {
    val loginEmail = varchar("login_email", length = 255)
    val hashedPassword = varchar("hashed_password", length = 255)
}

@Serializable
data class ExposedDevices(
    var userId: Int,
    var type: String
)

object Devices : IntIdTable("devices") {
    val userId = reference("userId", Users)
    val type = varchar("type", length = 255)
}

@Serializable
data class ExposedLocations(
    val deviceId: Int,
    val latitude: String,
    val longitude: String,
    @Contextual
    val timestamp: LocalDateTime
)

object Locations : IntIdTable("locations") {
    val deviceId = reference("deviceId", Devices)
    val latitude = varchar("latitude", length = 50)
    val longitude = varchar("longitude", length = 50)
    val timestamp = datetime("timestamp")
}

@Serializable
data class ExposedGeofences(
    val deviceId: Int
)

object Geofences : IntIdTable("geofences") {
    val deviceId = reference("device_id", Devices)
}

@Serializable
data class ExposedGeofenceVertices(
    val geofenceId: Int,
    val latitude: String,
    val longitude: String
)

object GeofenceVertices : IntIdTable("geofenceVertices") {
    val geofenceId = reference("geofence_id", Geofences)
    val latitude = varchar("latitude", length = 50)
    val longitude = varchar("longitude", length = 50)
}
