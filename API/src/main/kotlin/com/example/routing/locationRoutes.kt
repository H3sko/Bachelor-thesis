package com.example.routing

import com.example.data.service.DeviceService
import com.example.jwt.JWTService
import com.example.models.ExposedLocations
import com.example.models.LocationDto
import com.example.service.LocationsService
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.locationsDefault(jwtService: JWTService) {
    val locationsService = LocationsService()
    val userService = UserService()
    val deviceService = DeviceService()
    route("/location") {
        route("/latest/device/{deviceId}") {
            get {
                val deviceId = call.parameters["deviceId"]?.toInt()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceId?.let { _ -> deviceService.readById(deviceId) }

                if (device != null) {
                    if (device.userId == userId) {
                        val location = locationsService.getLatest(deviceId)
                        if (location != null) {
                            val locationDto = LocationDto(
                                    latitude = location.latitude,
                                    longitude = location.longitude,
                                    timestamp = location.timestamp
                                )
                            call.respond(HttpStatusCode.OK, locationDto)
                        } else {
                            call.respond(HttpStatusCode.Conflict, "Device $deviceId doesn't have any locations")
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Device $deviceId belongs to a different user")
                    }
                } else {
                    call.respond(HttpStatusCode.NotFound, "Device $deviceId doesn't exist")
                }
            }
        }
        route("/all/device/{deviceId}") {
            get {
                val deviceId = call.parameters["deviceId"]?.toInt()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val device = deviceId?.let { it1 -> deviceService.readById(it1) }

                val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Int.MAX_VALUE

                if (device != null) {
                    if (device.userId == userId) {
                        val locations = locationsService.getAll(deviceId, limit)
                        if (locations.isNotEmpty()) {
                            val locationDtos: List<LocationDto> = locations.map { loc ->
                                LocationDto(
                                    latitude = loc.latitude,
                                    longitude = loc.longitude,
                                    timestamp = loc.timestamp
                                )
                            }
                            call.respond(HttpStatusCode.OK, locationDtos)
                        } else {
                            call.respond(HttpStatusCode.Conflict, "This device doesn't have any locations")
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, "Device $deviceId belongs to a different user")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Device not found")
                }
            }
        }
    }
}

fun Route.locationsAdmin() {
    val locationsService = LocationsService()
    route("/location") {
        post {
            val location = call.receive<ExposedLocations>()

            if (locationsService.exists(location).not()) {
                val id = locationsService.create(location)
                call.respond(HttpStatusCode.Created, id)
            } else {
                call.respond(HttpStatusCode.Conflict, "Location already exists")
            }
        }
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toInt()

                if (id != null) {
                    val location = locationsService.read(id)
                    if (location != null) {
                        call.respond(HttpStatusCode.OK, location)
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
                    if (locationsService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Location deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Location not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("/deleteAll") {
            route("/{deviceId}") {
                delete {
                    val deviceId = call.parameters["deviceId"]?.toInt()

                    if (deviceId != null) {
                        val deletedLocations = locationsService.deleteAll(deviceId)
                        if (deletedLocations) {
                            call.respond(HttpStatusCode.OK, "Locations deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "This device doesn't have any locations")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId' parameter")
                    }
                }
            }
            delete {
                locationsService.deleteAll()
                call.respond(HttpStatusCode.OK, "All locations deleted")
            }
        }
        route("/getAll") {
            route("/{deviceId}") {
                get {
                    val deviceId = call.parameters["deviceId"]?.toInt()
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Int.MAX_VALUE

                    if (deviceId != null) {
                        val locations = locationsService.getAll(deviceId, limit)
                        if (locations.isNotEmpty()) {
                            call.respond(HttpStatusCode.OK, locations)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "This device doesn't have any locations")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId' parameter")
                    }
                }
            }
            get {
                val locations = locationsService.getAll()

                if (locations.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, locations)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Locations table is empty")
                }
            }
        }
    }
}
   