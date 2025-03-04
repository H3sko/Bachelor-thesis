package com.example

import com.example.firebase.FirebaseAdmin
import com.example.jwt.JWTService
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    FirebaseAdmin.init()
    configureHTTP()
    configureSerialization()
    configureDatabases()
    val jwtService = JWTService(this)

    configureAuthentication(jwtService)
    configureRouting(jwtService)
    configureQuartzScheduler()
}
