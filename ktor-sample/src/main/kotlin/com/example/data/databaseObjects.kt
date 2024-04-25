package com.example.data

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime


@Serializable
data class ExposedUsers(
    @Contextual
    val loginEmail: String,
    val password: String,
)

object Users : IntIdTable("users") {
    val loginEmail = varchar("loginEmail", length = 255)
    val password = varchar("password", length = 255)
}

@Serializable
data class ExposedDevices(
    var userId: Int,
    var name: String
)

object Devices : IntIdTable("devices") {
    val userId = reference("userId", Users)
    val name = varchar("name", length = 255)
}

@Serializable
data class ExposedLocations(
    val deviceId: Int,
    val latitude: String,
    val longitude: String,
    val timestamp: String
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
    val deviceId = reference("deviceId", Devices)
}

@Serializable
data class ExposedGeofenceVertices(
    val geofenceId: Int,
    val latitude: String,
    val longitude: String
)

object GeofenceVertices : IntIdTable("geofenceVertices") {
    val geofenceId = reference("geofenceId", Geofences)
    val latitude = varchar("latitude", length = 50)
    val longitude = varchar("longitude", length = 50)
}
