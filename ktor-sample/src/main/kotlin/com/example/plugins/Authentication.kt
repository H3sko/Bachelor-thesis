package com.example.plugins

import com.example.jwt.JWTService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*


fun Application.configureAuthentication(
    jwtService: JWTService
) {
    install(Authentication) {
        jwt("auth-user") {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                jwtService.userValidator(credential)
            }
        }
        jwt("auth-admin") {
            realm = jwtService.realm
            verifier(jwtService.jwtVerifier)

            validate { credential ->
                jwtService.adminValidator(credential)
            }
        }
    }
}