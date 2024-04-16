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

/* DONE */
fun Route.users() {
    val userService = UserService()
    route("/user/") {
        route("{id}"){
            get {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
                val user = userService.read(id)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            put {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
                val user = call.receive<ExposedUsers>()
                userService.update(id, user)
            }
            delete {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
        }
        route("login") {
            post {
                val user = call.receive<ExposedUsers>()
                val found = userService.login(user)
                if (found) {
                    call.respond(HttpStatusCode.Found, "User was found")
                } else {
                    call.respond(HttpStatusCode.NotFound, "User wasn't found")
                }
            }
        }
        route("register") {
            post {
                val user = call.receive<ExposedUsers>()
                if (userService.exists(user)) {
                    call.respond(HttpStatusCode.Conflict, "User already exists")
                } else {
                    val id: Int
                    try {
                        id = userService.create(user)
                        call.respond(HttpStatusCode.Created, id)
                    } catch (ex: ExposedSQLException) {
                        call.respond(HttpStatusCode.InternalServerError, ex.cause.toString())
                    }
                }
            }
        }
    }
}

/* DONE */
fun Route.devices() {
    val deviceService = DeviceService()
    route("/device/") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
                val device = deviceService.read(id)
                if (device != null) {
                    call.respond(HttpStatusCode.OK, device)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
            delete {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
        }
        route("/{userId}/{type}") {
            post {
                var device = call.receive<ExposedDevices>()
                device.userId = call.parameters["userId"]?.toInt() ?: throw IllegalArgumentException("invalidUserId")
                device.type = call.parameters["type"] ?: throw IllegalArgumentException("invalidType")
                val id = deviceService.create(device)
                call.respond(HttpStatusCode.Created, id)
            }
        }
    }
}

/* TODO */
fun Route.locations() {
    route("/location/") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
            put {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
            delete {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
        }
        post {}
    }
}

/* TODO */
fun Route.geofences() {
    route("/geofence/") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
            put {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
            delete {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
        }
        post {}
    }
}

/* TODO */
fun Route.geofenceVertices() {
    route("/vertex/") {
        route("{id}") {
            get {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
            put {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
            delete {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("invalidId")
            }
        }
        post {}
    }
}
