package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.datetime


object Users : IntIdTable("users") {
    val username = varchar("username", length = 255)
    val passwordHash = varchar("password", length = 255)
    val salt = binary("salt", length = 255)
}

// TODO: OnlineUsers
object OnlineUsers : IntIdTable("onlineUsers") {
    val userId = reference("userId", Users, onDelete = ReferenceOption.CASCADE)
    val token = varchar("token", length = 255)
//    val timestamp = datetime("timestamp")
}


object Devices : IntIdTable("devices") {
    val userId = reference("userId", Users, onDelete = ReferenceOption.CASCADE)
    val serialNumber = varchar("serialNumber", length = 255)
    val name = varchar("name", length = 255)
}


object Locations : IntIdTable("locations") {
    val deviceId = reference("deviceId", Devices, onDelete = ReferenceOption.CASCADE)
    val latitude = double("latitude")
    val longitude = double("longitude")
    val timestamp = datetime("timestamp")
}


object Geofences : IntIdTable("geofences") {
    val deviceId = reference("deviceId", Devices, onDelete = ReferenceOption.CASCADE)
}


object GeofenceVertices : IntIdTable("geofenceVertices") {
    val geofenceId = reference("geofenceId", Geofences, onDelete = ReferenceOption.CASCADE)
    val latitude = double("latitude")
    val longitude = double("longitude")
}