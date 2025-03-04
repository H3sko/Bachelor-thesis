package com.example.routing

import com.example.data.service.DeviceService
import com.example.jwt.JWTService
import com.example.models.DeviceCredentials
import com.example.models.ExposedDevices
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.devicesDefault(jwtService: JWTService) {
    val deviceService = DeviceService()
    val userService = UserService()
    route("/device") {
        post {

//                /* implementation for DataFetchJob*/
//                val deviceCredentials = call.receive<DeviceCredentials>()
//                if (deviceCredentials.name.isEmpty() || deviceCredentials.owner.isEmpty()){
//                    call.respond(HttpStatusCode.BadRequest, "Credentials cannot be empty")
//                }
//
//                val serialNumber: String? = exists(deviceCredentials)
//                if (serialNumber != null) {
//                    val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
//                        ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
//                    val userId = username?.let { it1 -> userService.getUserId(it1) }
//                    val deviceIsInDatabase = userId?.let { it1 -> deviceService.inDatabaseBySerialNumber(serialNumber) }
//
//                    if (deviceIsInDatabase != null) {
//                        if (deviceIsInDatabase.not()) {
//                            val deviceId = deviceService.create(ExposedDevices(userId, deviceCredentials.name, serialNumber))
//                            call.respond(HttpStatusCode.OK, deviceId)
//                        } else {
//                            call.respond(HttpStatusCode.Conflict, "This device is already in the database")
//                        }
//                    }
//                } else {
//                    call.respond(HttpStatusCode.NotFound, "Device ${deviceCredentials.name} that belongs to ${deviceCredentials.owner} was not found")
//                }

            /* implementation for DataAlterJob*/
            val deviceCredentials = call.receive<DeviceCredentials>()
            if (deviceCredentials.name.isNotEmpty() && deviceCredentials.owner.isNotEmpty()){
                val serialNumber: String = (deviceService.getAllIds().size + 1).toString()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
                val deviceIsInDatabase = userId?.let { _ -> deviceService.inDatabaseByNameAndOwner(deviceCredentials.name, userId) }

                if (deviceIsInDatabase != null) {
                    if (deviceIsInDatabase.not()) {
                        val deviceId = deviceService.create(ExposedDevices(userId, deviceCredentials.name, serialNumber))
                        call.respond(HttpStatusCode.OK, deviceId)
                    } else {
                        call.respond(HttpStatusCode.Conflict, "This device is already in the database")
                    }
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, "Credentials cannot be empty")
            }
        }
        route("/{id}") {
            delete {
                val id = call.parameters["id"]?.toInt()

                val deviceExists = id?.let { it1 -> deviceService.inDatabase(it1) }

                if (deviceExists != null) {
                    if (deviceExists) {
                        val deleted = deviceService.deleteDevice(id)
                        call.respond(HttpStatusCode.OK, deleted)
                    } else {
                        call.respond(HttpStatusCode.Conflict, false)
                    }
                }
            }
        }
        route("/all") {
            get {
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }

                if (userId != null) {
                    val devices = deviceService.getAll(userId)
                    call.respond(HttpStatusCode.OK, devices)
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
        route("/{serialNumber}") {
            get {
                val serialNumber = call.parameters["serialNumber"]

                if (serialNumber != null) {
                    val device = deviceService.read(serialNumber)
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
                val serialNumber = call.parameters["serialNumber"]
                serialNumber?.let {
                    if (deviceService.delete(serialNumber)) {
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
