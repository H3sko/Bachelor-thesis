package com.example.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database


fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://localhost:5431/postgresdb",
        driver = "org.postgresql.Driver",
        user = "postgres",
        password = "postgres"
    )
}