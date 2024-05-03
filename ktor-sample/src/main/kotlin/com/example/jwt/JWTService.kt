package com.example.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.models.ExposedUsers
import com.example.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.*
import java.util.*


class JWTService(
    private val application: Application
) {
    private val userService = UserService()

    private val audience = getConfigProperty("jwtDefault.audience")
    private val issuer = getConfigProperty("jwtDefault.issuer")
    val realm = getConfigProperty("jwtDefault.realm")
    private val secret = getConfigProperty("jwtDefault.secret")

    val jwtVerifier: JWTVerifier =
        JWT
            .require(Algorithm.HMAC256(secret))
            .withAudience(audience)
            .withIssuer(issuer)
            .build()

    suspend fun createJwtToken(user: ExposedUsers): String? {
        val found = userService.exists(user)

        return if (found) {
            JWT
                .create()
                .withAudience(audience)
                .withIssuer(issuer)
                .withClaim("username", user.username)
                .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000_000_000_000_000)) // TODO: zmenit len na 3_600_000
                .sign(Algorithm.HMAC256(secret))
        } else null
    }

    suspend fun adminValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractUsername(credential) ?: return null
        return if (username == "admin") {
            userValidator(credential)
        } else {
            null
        }
    }

    suspend fun userValidator(credential: JWTCredential): JWTPrincipal? {
        val username = extractUsername(credential) ?: return null
        val foundUser = userService.getUser(username)

        return foundUser?.let {
            if (credential.payload.audience.contains(audience)) {
                JWTPrincipal(credential.payload)
            } else null
        }
    }


    suspend fun extractUsernameFromToken(token: String): String? {
        val payload = createJWTCredential(token)
        val username = extractUsername(payload)

        return username
    }


    fun createJWTCredential(token: String): JWTCredential {
        val decodedJWT: DecodedJWT = JWT.decode(token)
        return JWTCredential(decodedJWT)
    }


    fun extractUsername(credential: JWTCredential): String? {
        return credential.payload.getClaim("username").asString()
    }

    private fun getConfigProperty(path: String) =
        application.environment.config.propertyOrNull(path).toString()
}
