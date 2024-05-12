package com.example.plugins

import com.example.jwt.JWTService
import com.example.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureRouting(jwtService: JWTService) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        route("/"){
            get {
                call.respond(HttpStatusCode.OK, "Working")
            }
        }
        usersDefault(jwtService)
        authenticate("auth-user") {
            devicesDefault(jwtService)
            locationsDefault(jwtService)
            geofencesDefault(jwtService)
            geofenceVerticesDefault(jwtService)
        }
        authenticate("auth-admin") {
            devicesDefault(jwtService)
            locationsDefault(jwtService)
            geofencesDefault(jwtService)
            geofenceVerticesDefault(jwtService)

            usersAdmin()
            devicesAdmin()
            locationsAdmin()
            geofencesAdmin()
            geofenceVerticesAdmin()
        }
    }
}
