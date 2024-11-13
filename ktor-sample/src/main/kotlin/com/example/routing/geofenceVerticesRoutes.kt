package com.example.routing

import com.example.data.service.DeviceService
import com.example.jwt.JWTService
import com.example.models.ExposedGeofenceVertices
import com.example.models.GeofenceVertexRequest
import com.example.service.GeofenceService
import com.example.service.GeofenceVerticesService
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.geofenceVerticesDefault(jwtService: JWTService) {
    val geofenceVerticesService = GeofenceVerticesService()
    val userService = UserService()
    val deviceService = DeviceService()
    val geofenceService = GeofenceService()
    route("/vertex") {
        route("/add") {
            post {
                val geofenceVertex = call.receive<ExposedGeofenceVertices>()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val geofence = geofenceService.read(geofenceVertex.geofenceId)
                val device = geofence?.let { it1 -> deviceService.readById(it1.deviceId) }

                if (device != null) {
                    if (device.userId == userId) {
                        val exists = geofenceVerticesService.exists(geofenceVertex)
                        if (exists.not()) {
                            val id = geofenceVerticesService.create(geofenceVertex)
                            call.respond(HttpStatusCode.Created, id)
                        } else {
                            call.respond(HttpStatusCode.Conflict, "Vertex for this geofence already exists")
                        }
                    } else {
                        call.respond(HttpStatusCode.Conflict, "This Geofence belongs to different device")
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
        route("/deleteAll") {
            delete {
                val geofenceId = call.receive<GeofenceVertexRequest>().geofenceId
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val geofence = geofenceService.read(geofenceId)
                val device = geofence?.let { it1 -> deviceService.readById(it1.deviceId) }

                if (device != null) {
                    if (device.userId == userId) {
                        val deleted = geofenceVerticesService.deleteAll(geofenceId)
                        if (deleted) {
                            call.respond(HttpStatusCode.OK, "All vertices deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "This geofence doesn't have any vertices")
                        }
                    } else {
                        call.respond(HttpStatusCode.Conflict, "This Geofence belongs to different device")
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
        route("/getAll") {
            get {
                val geofenceId = call.receive<GeofenceVertexRequest>().geofenceId
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val geofence = geofenceService.read(geofenceId)
                val device = geofence?.let { it1 -> deviceService.readById(it1.deviceId) }

                if (device != null) {
                    if (device.userId == userId) {
                        val vertices = geofenceVerticesService.getAll(geofenceId)
                        if (vertices.isNotEmpty()) {
                            call.respond(HttpStatusCode.OK, vertices)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "This geofence doesn't have any vertices")
                        }
                    } else {
                        call.respond(HttpStatusCode.Conflict, "This Geofence belongs to different device")
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized)
                }
            }
        }
    }
}


fun Route.geofenceVerticesAdmin() {
    val geofenceVerticesService = GeofenceVerticesService()
    route("/vertex") {
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toInt()

                if (id != null) {
                    val vertex = geofenceVerticesService.read(id)
                    if (vertex != null) {
                        call.respond(HttpStatusCode.OK, vertex)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'id' parameter")
                }
            }

            delete {
                val id = call.parameters["id"]?.toInt()
                id?.let {
                    if (geofenceVerticesService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Vertex deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Vertex not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("/deleteAllVertices") {
            delete {
                geofenceVerticesService.deleteAll()
                call.respond(HttpStatusCode.OK, "All vertices deleted")
            }
        }
        route("/getAllVertices") {
            get {
                val geofenceVertices = geofenceVerticesService.getAll()

                if (geofenceVertices.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, geofenceVertices)
                } else {
                    call.respond(HttpStatusCode.NotFound, "geofenceVertices table is empty")
                }
            }
        }
    }
}