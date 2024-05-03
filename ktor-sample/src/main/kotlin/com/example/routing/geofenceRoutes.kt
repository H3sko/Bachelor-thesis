package com.example.routing

import com.example.jwt.JWTService
import com.example.models.ExposedGeofences
import com.example.service.DeviceService
import com.example.service.GeofenceService
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.geofencesDefault(jwtService: JWTService) {
    val geofenceService = GeofenceService()
    val userService = UserService()
    val deviceService = DeviceService()
    route("/geofence") {
        route("/add") {
            post {
                val geofence = call.receive<ExposedGeofences>()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceService.read(geofence.deviceId)

                if (device != null){
                    if (device.userId == userId) {
                        if (geofenceService.exists(geofence).not()) {
                            geofenceService.create(geofence)
                            call.respond(HttpStatusCode.OK, "Geofence created")
                        } else {
                            call.respond(HttpStatusCode.Conflict, "This device already has a geofence")
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Device ${geofence.deviceId} belongs to other user")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Device ${geofence.deviceId} not found")
                }
            }
        }
        route("/device") {
            get {
                val deviceId = call.receive<ExposedGeofences>().deviceId
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceService.read(deviceId)

                if (device != null){
                    if (device.userId == userId) {
                        val id = geofenceService.getGeofence(deviceId)
                        if (id != null) {
                            call.respond(HttpStatusCode.OK, id)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Device $deviceId belongs to other user")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Device $deviceId not found")
                }
            }
            delete {
                val deviceId = call.receive<ExposedGeofences>().deviceId
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceService.read(deviceId)

                if (device != null){
                    if (device.userId == userId) {
                        geofenceService.removeGeofence(deviceId)
                        call.respond(HttpStatusCode.OK, "Geofence deleted")
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Device $deviceId belongs to other user")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Device $deviceId not found")
                }
            }
        }
    }
}

fun Route.geofencesAdmin() {
    val geofenceService = GeofenceService()
    route("/geofence") {
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toInt()

                if (id != null) {
                    val deviceId = geofenceService.read(id)
                    if (deviceId != null) {
                        call.respond(HttpStatusCode.OK, deviceId)
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Geofence not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
                }
            }
            delete {
                val id = call.parameters["id"]?.toInt()
                id?.let {
                    if (geofenceService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Geofence deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Geofence not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("deleteAll") {
            delete {
                geofenceService.deleteAll()
                call.respond(HttpStatusCode.OK, "All geofences deleted")
            }
        }
        route("getAll") {
            get {
                val geofences = geofenceService.getAll()

                if (geofences.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, geofences)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Geofences table is empty")
                }
            }
        }
    }
}