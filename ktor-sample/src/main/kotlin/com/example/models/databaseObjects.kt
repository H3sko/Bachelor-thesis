package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime


object Users : IntIdTable("users") {
    val username = varchar("username", length = 255)
    val password = varchar("password", length = 255)
}

object Devices : IntIdTable("devices") {
    val userId = reference("userId", Users)
    val name = varchar("name", length = 255)
}

object Locations : IntIdTable("locations") {
    val deviceId = reference("deviceId", Devices)
    val latitude = varchar("latitude", length = 50)
    val longitude = varchar("longitude", length = 50)
    val timestamp = datetime("timestamp")
}

object Geofences : IntIdTable("geofences") {
    val deviceId = reference("deviceId", Devices)
}

object GeofenceVertices : IntIdTable("geofenceVertices") {
    val geofenceId = reference("geofenceId", Geofences)
    val latitude = varchar("latitude", length = 50)
    val longitude = varchar("longitude", length = 50)
}