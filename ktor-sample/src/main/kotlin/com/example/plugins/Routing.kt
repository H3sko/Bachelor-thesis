package com.example.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.example.models.*
import com.example.data.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    routing {
        users()
        devices()
        locations()
        geofences()
        geofenceVertices()
    }
}


fun Route.users() {
    val userService = UserService()
    route("/user/") {
        route("{id}"){
            get {
                val id = call.parameters["id"]?.toInt()

                if (id != null) {
                    val user = userService.read(id)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing or invalid 'id' parameter")
                }
            }

            delete {
                val id = call.parameters["id"]?.toInt()
                id?.let {
                    userService.delete(id)
                    call.respond(HttpStatusCode.OK, "User deleted")
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("changeCredentials/{login}/{password}") {
            put {
                val id = call.parameters["id"]?.toInt()
                val login = call.parameters["login"]
                val password = call.parameters["password"]

                if (id != null && login != null && password != null) {
                    userService.update(id, login, password)
                    call.respond(HttpStatusCode.OK, "Credentials updated")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid credentials")
                }
            }

        }
        route("login/{login}/{password}") {
            post {
                val login = call.parameters["login"]
                val password = call.parameters["password"]

                if (login != null && password != null) {
                    val found = userService.login(login, password)
                    if (found) {
                        call.respond(HttpStatusCode.Found, "User was found")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User wasn't found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'login' or 'password' parameter")
                }
            }

        }
        route("register/{login}/{password}") {
            post {
                val login = call.parameters["login"]
                val password = call.parameters["password"]

                if (login != null && password != null) {
                    if (userService.exists(login)) {
                        call.respond(HttpStatusCode.Conflict, "User already exists")
                    } else {
                        try {
                            val id = userService.create(login, password)
                            call.respond(HttpStatusCode.Created, id)
                        } catch (ex: ExposedSQLException) {
                            call.respond(HttpStatusCode.InternalServerError, ex.cause.toString())
                        }
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'login' or 'password' parameter")
                }
            }

        }
    }
}


fun Route.devices() {
    val deviceService = DeviceService()
    route("/device/") {
        route("{id}") {
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
                    deviceService.delete(id)
                    call.respond(HttpStatusCode.OK, "Device deleted")
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("{userId}/") {
            route("add/{type}") {
                post {
                    val userId = call.parameters["userId"]?.toInt()
                    val type = call.parameters["type"]

                    if (userId != null && type != null) {
                        val id = deviceService.create(userId, type)
                        call.respond(HttpStatusCode.Created, id)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'userId' or 'type' parameter")
                    }
                }

            }
            route("getAll") {
                get {
                    val userId = call.parameters["userId"]?.toInt()

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
}


fun Route.locations() {
    val locationsService = LocationsService()
    route("/location/") {
        route("{id}") {
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
                    locationsService.delete(id)
                    call.respond(HttpStatusCode.OK, "Location deleted")
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("/{deviceId}"){
            route("/{timestamp}/{latitude}/{longitude}") {
                post {
                    val deviceId = call.parameters["deviceId"]?.toInt()
                    val latitude = call.parameters["latitude"]
                    val longitude = call.parameters["longitude"]
                    val timestampString = call.parameters["timestamp"]

                    if (deviceId != null && latitude != null && longitude != null && timestampString != null) {
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        try {
                            val timestamp = LocalDateTime.parse(timestampString, formatter)
                            val id = locationsService.create(deviceId, latitude, longitude, timestamp)
                            call.respond(HttpStatusCode.Created, id)
                        } catch (ex: DateTimeParseException) {
                            call.respond(HttpStatusCode.BadRequest, "Invalid timestamp format")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId', 'latitude', 'longitude', or 'timestamp' parameter")
                    }
                }

            }
            route("/getLatest") {
                get {
                    val deviceId = call.parameters["deviceId"]?.toInt()

                    if (deviceId != null) {
                        val location = locationsService.getLatest(deviceId)
                        if (location != null) {
                            call.respond(HttpStatusCode.OK, location)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId' parameter")
                    }
                }

            }
        }
    }
}


fun Route.geofences() {
    val geofenceService = GeofenceService()
    route("/geofence/") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toInt()

                if (id != null) {
                    val deviceId = geofenceService.read(id)
                    if (deviceId != null) {
                        call.respond(HttpStatusCode.OK, deviceId)
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
                    geofenceService.delete(id)
                    call.respond(HttpStatusCode.OK, "Geofence deleted")
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("{deviceId}") {
            get {
                val deviceId = call.parameters["deviceId"]?.toInt()

                if (deviceId != null) {
                    val id = geofenceService.getGeofence(deviceId)
                    if (id != null) {
                        call.respond(HttpStatusCode.OK, id)
                    } else {
                        call.respond(HttpStatusCode.NotFound)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId' parameter")
                }
            }

            post {
                val deviceId = call.parameters["deviceId"]?.toInt()

                if (deviceId != null) {
                    val geofence = geofenceService.getGeofence(deviceId)
                    if (geofence != null) {
                        call.respond(HttpStatusCode.Conflict, "Geofence for this device already exists")
                    } else{
                        val id = geofenceService.create(deviceId)
                        call.respond(HttpStatusCode.Created, id)
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId' parameter")
                }
            }

        }
    }
}


fun Route.geofenceVertices() {
    val geofenceVerticesService = GeofenceVerticesService()
    route("/vertex/") {
        route("{id}") {
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
                    geofenceVerticesService.delete(id)
                    call.respond(HttpStatusCode.OK, "Geofence vertex deleted")
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("{geofenceId}") {
            route("/getAll") {
                get {
                    val geofenceId = call.parameters["geofenceId"]?.toInt()

                    if (geofenceId != null) {
                        val vertices = geofenceVerticesService.getAll(geofenceId)
                        if (vertices.isNotEmpty()) {
                            call.respond(HttpStatusCode.OK, vertices)
                        } else {
                            call.respond(HttpStatusCode.NotFound)
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'geofenceId' parameter")
                    }
                }
            }

            route("/{latitude}/{longitude}") {
                post {
                    val geofenceId = call.parameters["geofenceId"]?.toInt()
                    val latitude = call.parameters["latitude"]
                    val longitude = call.parameters["longitude"]

                    if (geofenceId != null && latitude != null && longitude != null) {
                        val id = geofenceVerticesService.create(geofenceId, latitude, longitude)
                        call.respond(HttpStatusCode.Created, id)
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'latitude' or 'longitude' parameter")
                    }
                }
            }
        }
    }
}
