package com.example.routing

import com.example.jwt.JWTService
import com.example.models.ExposedUsers
import com.example.models.UserRequest
import com.example.service.UserService
import com.example.utils.generateSalt
import com.example.utils.sha256WithSalt
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException


fun Route.usersDefault(jwtService: JWTService) {
    val userService = UserService()
    route("/user") {
        route("/login") {
            post {
                val user = call.receive<UserRequest>()
                if (user.username.isNotEmpty() && user.password.isNotEmpty()) {
                    val token = jwtService.createJwtToken(user)

                    token?.let {
                        call.respond(HttpStatusCode.OK, hashMapOf("token" to token))
                    } ?: call.respond(HttpStatusCode.Unauthorized, "Invalid token")
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Credentials cannot be empty")
                }
            }
        }
        route("/register") {
            post {
                val user = call.receive<UserRequest>()
                if (user.username.isNotEmpty() && user.password.isNotEmpty()) {
                    if (userService.getUserId(user.username) == null) {
                        try {
                            val salt = generateSalt(16)
                            val passwordHash = sha256WithSalt(user.password, salt)
                            val id = userService.create(ExposedUsers(user.username, passwordHash, salt))
                            call.respond(HttpStatusCode.Created, id)
                        } catch (ex: ExposedSQLException) {
                            call.respond(HttpStatusCode.InternalServerError, ex.cause.toString())
                        }
                    } else {
                        call.respond(HttpStatusCode.Conflict, "User already exists")
                    }
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Credentials cannot be empty")
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