package com.example.routing

import com.example.jwt.JWTService
import com.example.models.DeviceNameRequest
import com.example.models.ExposedDevices
import com.example.service.DeviceService
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.devicesDefault(jwtService: JWTService) {
    val deviceService = DeviceService()
    val userService = UserService()
    route("/device") {
        route("/add") {
            post {
                val deviceName = call.receive<DeviceNameRequest>().name

                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val deviceExists = userId?.let { it1 -> deviceService.exists(ExposedDevices(it1, deviceName)) }

                if (deviceExists != null) {
                    if (deviceExists.not()) {
                        val deviceId = deviceService.create(ExposedDevices(userId, deviceName))
                        call.respond(HttpStatusCode.OK, deviceId)
                    } else {
                        call.respond(HttpStatusCode.Conflict, "$username already has a device named $deviceName")
                    }
                }
            }
        }
        route("/remove") {
            delete {
                val deviceName = call.receive<DeviceNameRequest>().name

                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val deviceExists = userId?.let { it1 -> deviceService.exists(ExposedDevices(it1, deviceName)) }

                if (deviceExists != null) {
                    if (deviceExists) {
                        deviceService.deleteDevice(ExposedDevices(userId, deviceName))
                        call.respond(HttpStatusCode.OK, "$deviceName deleted")
                    } else {
                        call.respond(HttpStatusCode.Conflict, "$username doesn't have a device named $deviceName")
                    }
                }
            }
        }
        route("/getUserDevices") {
            get {
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }

                if (userId != null) {
                    val devices = deviceService.getAll(userId)
                    if (devices.isNotEmpty()) {
                        call.respond(HttpStatusCode.OK, devices)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'userId' parameter")
                }
            }
        }
    }
}

fun Route.devicesAdmin() {
    val deviceService = DeviceService()
    route("/device") {
        route("/{id}") {
            get {
                val id = call.parameters["id"]?.toInt()

                if (id != null) {
                    val device = deviceService.read(id)
                    if (device != null) {
                        call.respond(HttpStatusCode.OK, device)
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
                    if (deviceService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Device deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Device not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("/deleteAll") {
            delete {
                deviceService.deleteAllDevices()
                call.respond(HttpStatusCode.OK, "All devices deleted")
            }
            route("/{userId}") {
                delete {
                    val userId = call.parameters["userId"]?.toInt()

                    if (userId != null) {
                        deviceService.deleteAllDevices(userId)
                        call.respond(HttpStatusCode.OK, "All devices deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "ID not found")
                    }
                }
            }
        }
        route("/getAll") {
            get {
                val devices = deviceService.getAllDevices()

                if (devices.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, devices)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Device table is empty")
                }
            }
        }
    }
}
