package com.example.routing

import com.example.data.service.DeviceService
import com.example.jwt.JWTService
import com.example.models.ExposedGeofenceVertices
import com.example.models.ExposedGeofences
import com.example.models.GeofenceVertex
import com.example.service.GeofenceService
import com.example.service.GeofenceVerticesService
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
    val geofenceVerticesService = GeofenceVerticesService()
    route("/geofence") {
        route("/add/{deviceId}") {
            post {
                val deviceId = call.parameters["deviceId"]?.toInt()
                val vertices = call.receive<List<GeofenceVertex>>()
                if (vertices.size == 3) {
                    val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                    val userId = username?.let { it1 -> userService.getUserId(it1) }
                    val device = deviceId?.let { it1 -> deviceService.readById(it1) }

                    if (device != null){
                        if (device.userId == userId) {
                            if (geofenceService.exists(ExposedGeofences(deviceId)).not()) {
                                val geofenceId = geofenceService.create(ExposedGeofences(deviceId))
                                for (vertex in vertices) {
                                    geofenceVerticesService.create(ExposedGeofenceVertices(geofenceId, vertex.latitude, vertex.longitude))
                                }
                                call.respond(HttpStatusCode.OK, "Geofence created")
                            } else {
                                call.respond(HttpStatusCode.Conflict, "This device already has a geofence")
                            }
                        } else {
                            call.respond(HttpStatusCode.Unauthorized, "Device $deviceId belongs to other user")
                        }
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Device $deviceId not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Geofence needs at least 3 vertices")

                }
            }
        }
        route("/device/{deviceId}") {
            get {
                val deviceId = call.parameters["deviceId"]?.toInt()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceId?.let { it1 -> deviceService.readById(it1) }

                if (device != null){
                    if (device.userId == userId) {
                        val geofenceId = geofenceService.getGeofence(deviceId)
                        if (geofenceId != null) {
                            val vertices: List<GeofenceVertex> = geofenceVerticesService.getAll(geofenceId).map { GeofenceVertex(it.latitude, it.longitude) }
                            call.respond(HttpStatusCode.OK, vertices)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Device $deviceId belongs to other user")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Device $deviceId not found")
                }
            }
        }
        route("/delete/device/{deviceId}") {
            delete {
                val deviceId = call.parameters["deviceId"]?.toInt()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceId?.let { it1 -> deviceService.readById(it1) }

                if (device != null){
                    if (device.userId == userId) {
                        geofenceService.removeGeofence(deviceId)
                        call.respond(HttpStatusCode.OK, "Device $deviceId deleted")
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
        route("/deleteAll") {
            delete {
                geofenceService.deleteAll()
                call.respond(HttpStatusCode.OK, "All geofences deleted")
            }
        }
        route("/getAll") {
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