package com.example.routing

import com.example.data.service.OnlineUserService
import com.example.jwt.JWTService
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.routing.*


fun Route.onlineUsersDefault(jwtService: JWTService) {
    // TODO: OnlineUsers
    val userService = UserService()
    val onlineUserService = OnlineUserService()
    route("/onlineUser") {
        route("/add") {
            post {
                // TODO
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
            }
        }
        route("/remove") {
            delete {
                // TODO
                val username = call.request.headers["Authorization"]?.removePrefix("Bearer ")
                    ?.let { it1 -> jwtService.extractUsernameFromToken(it1) }
                val userId = username?.let { it1 -> userService.getUserId(it1) }
            }
        }
    }
}