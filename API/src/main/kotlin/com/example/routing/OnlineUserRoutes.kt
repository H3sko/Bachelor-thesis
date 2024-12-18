package com.example.routing

import com.example.data.service.OnlineUserService
import com.example.jwt.JWTService
import com.example.models.ExposedOnlineUser
import com.example.models.OnlineUserRequest
import com.example.models.SwitchRequest
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.onlineUsersDefault(jwtService: JWTService) {
    val userService = UserService()
    val onlineUserService = OnlineUserService()

    route("/online-user") {
        post {
            val payload = call.receive<OnlineUserRequest>()
            val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
            val userId = username?.let { it1 -> userService.getUserId(it1) }

            if (payload.token != "") {
                if (userId != null) {
                    val onlineUser = onlineUserService.getOnlineUserByUserId(userId.toInt())
                    if (onlineUser != null) {
                        onlineUserService.updateToken(userId.toInt(), payload.token)
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Users token has been updated"))
                    } else {
                        onlineUserService.create(ExposedOnlineUser(userId.toInt(), payload.token, payload.activeNotification))
                        call.respond(HttpStatusCode.Created, mapOf("message" to "User is now online"))
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid token"))
                }
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Empty token received"))
            }
        }
        delete {
            val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
            val userId = username?.let { it1 -> userService.getUserId(it1) }

            if (userId != null) {
                onlineUserService.removeUserOnline(userId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "User is now offline"))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid token"))
            }
        }
        route("/notification") {
            route("/switch") {
                put {
                    val payload = call.receive<SwitchRequest>()
                    val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                    val userId = username?.let { it1 -> userService.getUserId(it1) }

                    if (userId != null) {
                        val online = onlineUserService.getOnlineUserByUserId(userId)
                        if (online != null) {
                            onlineUserService.switchNotification(userId, payload.activeNotification)
                            call.respond(HttpStatusCode.OK, payload.activeNotification)
                        } else {
                            call.respond(HttpStatusCode.Conflict, mapOf("message" to "User is offline"))
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid token"))
                    }
                }
            }
            route("/status") {
                get {
                    val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                        ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                    val userId = username?.let { it1 -> userService.getUserId(it1) }

                    if (userId != null) {
                        val online = onlineUserService.getOnlineUserByUserId(userId)
                        val status = onlineUserService.getNotificationStatus(userId)
                        if (online != null && status != null) {
                            call.respond(HttpStatusCode.OK, status)
                        } else {
                            call.respond(HttpStatusCode.Conflict, mapOf("message" to "User is offline"))
                        }
                    } else {
                        call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid token"))
                    }
                }
            }
        }
    }
}

fun Route.onlineUsersAdmin() {
    val onlineUserService = OnlineUserService()

    route("/online-user") {
        route("/add/{userId}") {
            post {
                val payload = call.receive<OnlineUserRequest>()
                val userId = call.parameters["userId"]

                if (payload.token != "" && userId != null) {
                    val onlineUser = onlineUserService.getOnlineUserByUserId(userId.toInt())
                    if (onlineUser != null) {
                        onlineUserService.updateToken(userId.toInt(), payload.token)
                        call.respond(HttpStatusCode.OK, "Users token has been updated")
                    } else {
                        onlineUserService.create(ExposedOnlineUser(userId.toInt(), payload.token, payload.activeNotification))
                        call.respond(HttpStatusCode.Created, "User is now online")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Missing credentials")

                }
            }
        }
        route("/remove/{userId}") {
            delete {
                val userId = call.parameters["userId"]?.toIntOrNull()
                if (userId != null) {
                    val success = onlineUserService.removeUserOnline(userId)
                    if (success) {
                        call.respond(HttpStatusCode.OK, "User removed")
                    } else {
                        call.respond(HttpStatusCode.NotFound, "User not found")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
                }
            }
        }
        route("/deleteAll") {
            delete {
                onlineUserService.deleteAllOnlineUsers()
                call.respond(HttpStatusCode.OK, "All online users deleted")
            }
        }
        route("/getAll") {
            get {
                val onlineUsers = onlineUserService.getAllOnlineUsers()
                call.respond(HttpStatusCode.OK, onlineUsers)
            }
        }
    }
}
