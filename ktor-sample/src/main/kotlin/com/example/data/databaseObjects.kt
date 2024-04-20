package com.example.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime


@Serializable
data class ExposedUsers(
    val id: Int,
    val loginEmail: String,
    val password: String,
)

object Users : IntIdTable("users") {
    val loginEmail = varchar("login_email", length = 255)
    val password = varchar("password", length = 255)
}

@Serializable
data class ExposedDevices(
    val id: Int,
    var userId: Int,
    var name: String
)

object Devices : IntIdTable("devices") {
    val userId = reference("userId", Users)
    val name = varchar("type", length = 255)
}

@Serializable
data class ExposedLocations(
    val id: Int,
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
    val id: Int,
    val deviceId: Int
)

object Geofences : IntIdTable("geofences") {
    val deviceId = reference("device_id", Devices)
}

@Serializable
data class ExposedGeofenceVertices(
    val id: Int,
    val geofenceId: Int,
    val latitude: String,
    val longitude: String
)

object GeofenceVertices : IntIdTable("geofenceVertices") {
    val geofenceId = reference("geofence_id", Geofences)
    val latitude = varchar("latitude", length = 50)
    val longitude = varchar("longitude", length = 50)
}
