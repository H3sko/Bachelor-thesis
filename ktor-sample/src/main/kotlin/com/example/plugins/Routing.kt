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
                    if (userService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "User deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("changeCredentials/{login}/{password}") {
            put {
                val user = call.receive<ExposedUsers>()
                val login = call.parameters["login"]
                val password = call.parameters["password"]

                if (login != null && password != null) {
                    if ((userService.exists(login)) == null) {
                        if (userService.login(user)) {
                            userService.update(user.loginEmail, user.password, login, password)
                            call.respond(HttpStatusCode.OK, "Credentials updated")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "Wrong login or password")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "User with the same login already exists")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid credentials")
                }
            }

        }
        route("login") {
            get {
                val user = call.receive<ExposedUsers>()

                val found = userService.login(user)
                if (found) {
                    call.respond(HttpStatusCode.OK, "Logged in")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User wasn't found")
                }
            }
        }
        route("register") {
            post {
                val user = call.receive<ExposedUsers>()

                if (userService.exists(user.loginEmail) != null) {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                } else {
                    try {
                        val id = userService.create(user)
                        call.respond(HttpStatusCode.Created, id)
                    } catch (ex: ExposedSQLException) {
                        call.respond(HttpStatusCode.InternalServerError, ex.cause.toString())
                    }
                }
            }
        }
        route("deleteAll") {
            delete {
                userService.deleteAllUsers()
                call.respond(HttpStatusCode.OK, "All users deleted")
            }
        }
        route("getAll") {
            get {
                val users = userService.getAllUsers()

                if (users.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, users)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Users table is empty")
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
                    if (deviceService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Device deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Device not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("add") {
            post {
                val device = call.receive<ExposedDevices>()

                if (deviceService.exists(device).not()) {
                    val id = deviceService.create(device)
                    call.respond(HttpStatusCode.Created, id)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Device already exists")
                }
            }
        }
        route("getAll/{userId}") {
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
        route("deleteAll") {
            delete {
                deviceService.deleteAllDevices()
                call.respond(HttpStatusCode.OK, "All devices deleted")
            }
        }
        route("getAll") {
            get {
                val devices = deviceService.getAllDevices()

                if (devices.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, devices)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Devices table is empty")
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
                    if (locationsService.delete(id)) {
                        call.respond(HttpStatusCode.OK, "Location deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "Location not found")
                    }
                } ?: call.respond(HttpStatusCode.NotFound, "Invalid ID")
            }
        }
        route("/add") {
            post {
                val location = call.receive<ExposedLocations>()

                if (locationsService.exists(location).not()) {
                    val id = locationsService.create(location)
                    call.respond(HttpStatusCode.Created, id)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Location already exists")
                }
            }
        }
        route("getLatest/{deviceId}") {
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
        route("deleteAll") {
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
        route("getAll") {
            get {
                val locations = locationsService.getAll()

                if (locations.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, locations)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Devices table is empty")
                }
            }
        }
    }
}


fun Route.geofences() {
    val geofenceService = GeofenceService()
    route("/geofence") {
        route("/add") {
            post {
                val geofence = call.receive<ExposedGeofences>()

                val exists = geofenceService.exists(geofence)
                if (exists.not()) {
                    val id = geofenceService.create(geofence)
                    call.respond(HttpStatusCode.Created, id)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Geofence for this device already exists")
                }
            }
        }
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
        route("device/{deviceId}") {
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
            delete {
                val deviceId = call.parameters["deviceId"]?.toInt()

                if (deviceId != null) {
                    val deleted = geofenceService.removeGeofence(deviceId)
                    if (deleted) {
                        call.respond(HttpStatusCode.OK, "Geofence deleted")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "This device doesn't have a geofence")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing 'deviceId' parameter")
                }
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


fun Route.geofenceVertices() {
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
        route("/add") {
            post {
                val geofenceVertex = call.receive<ExposedGeofenceVertices>()

                val exists = geofenceVerticesService.exists(geofenceVertex)
                if (exists.not()) {
                    val id = geofenceVerticesService.create(geofenceVertex)
                    call.respond(HttpStatusCode.Created, id)
                } else {
                    call.respond(HttpStatusCode.Conflict, "Vertex for this geofence already exists")
                }
            }
        }
        route("/deleteAll") {
            delete {
                geofenceVerticesService.deleteAll()
                call.respond(HttpStatusCode.OK, "All vertices deleted")
            }
            route("/{geofenceId}") {
                delete {
                    val geofenceId = call.parameters["geofenceId"]?.toInt()

                    if (geofenceId != null) {
                        val deleted = geofenceVerticesService.deleteAll(geofenceId)
                        if (deleted) {
                            call.respond(HttpStatusCode.OK, "All vertices deleted")
                        } else {
                            call.respond(HttpStatusCode.NotFound, "This geofence doesn't have any vertices")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'geofenceId' parameter")
                    }
                }
            }
        }
        route("/getAll") {
            get {
                val geofenceVertices = geofenceVerticesService.getAll()

                if (geofenceVertices.isNotEmpty()) {
                    call.respond(HttpStatusCode.Found, geofenceVertices)
                } else {
                    call.respond(HttpStatusCode.NotFound, "geofenceVertices table is empty")
                }
            }
            route("/{geofenceId}") {
                get {
                    val geofenceId = call.parameters["geofenceId"]?.toInt()

                    if (geofenceId != null) {
                        val vertices = geofenceVerticesService.getAll(geofenceId)
                        if (vertices.isNotEmpty()) {
                            call.respond(HttpStatusCode.OK, vertices)
                        } else {
                            call.respond(HttpStatusCode.NotFound, "This geofence doesn't have any vertices")
                        }
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Missing 'geofenceId' parameter")
                    }
                }
            }
        }
    }
}
