package com.example.routing

import com.example.jwt.JWTService
import com.example.models.ExposedUsers
import com.example.models.PasswordChangeRequest
import com.example.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException


fun Route.usersDefault(jwtService: JWTService) {
    val userService = UserService()
    route("/user") {
        route("/login") {
            get {
                val user = call.receive<ExposedUsers>()

                val token = jwtService.createJwtToken(user)

                token?.let {
                    call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
                } ?: call.respond(HttpStatusCode.Unauthorized)
            }
        }
        route("/register") {
            post {
                val user = call.receive<ExposedUsers>()

                if (userService.getUser(user.username) != null) {
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
        route("/changePassword") {
            put {
                val user = call.receive<PasswordChangeRequest>()
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }

                if (username != null) {
                    if (userService.exists(ExposedUsers(user.username, user.oldPassword)) and (username == user.username)) {
                        userService.update(user)
                        call.respond(HttpStatusCode.OK, "Password changed")
                    } else {
                        call.respond(HttpStatusCode.BadRequest, "Wrong username or password")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Invalid credentials")
                }
            }
        }
    }
}

fun Route.usersAdmin(){
    val userService = UserService()
    route("/user") {
        route("/{id}") {
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
        route("/deleteAll") {
            delete {
                userService.deleteAllUsers()
                call.respond(HttpStatusCode.OK, "All users deleted")
            }
        }
        route("/getAll") {
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