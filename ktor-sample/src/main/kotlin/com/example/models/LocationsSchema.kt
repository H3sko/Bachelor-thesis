package com.example.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

class LocationsService {
    init {
        transaction {
            SchemaUtils.create(Locations)
        }
    }

    suspend fun getLatest(deviceId: Int): ExposedLocations? {
        return dbQuery {
            Locations.selectAll().where { Locations.deviceId eq deviceId }
                .maxByOrNull { it[Locations.timestamp] }
                ?.let {
                    ExposedLocations(
                        it[Locations.id].value,
                        it[Locations.deviceId].value,
                        it[Locations.latitude],
                        it[Locations.longitude],
                        it[Locations.timestamp]
                    )
                }
        }
    }


    suspend fun create(deviceId: Int, latitude: String, longitude: String, timestamp: LocalDateTime): Int = dbQuery {
        Locations.insertAndGetId {
            it[Locations.deviceId] = EntityID(deviceId, Locations)
            it[Locations.latitude] = latitude
            it[Locations.longitude] = longitude
            it[Locations.timestamp] = timestamp
        }.value
    }


    suspend fun read(id: Int): ExposedLocations? {
        return dbQuery {
            Locations.selectAll().where { Locations.id eq id }
                .map {
                    ExposedLocations(
                        it[Locations.id].value,
                        it[Locations.deviceId].value,
                        it[Locations.latitude],
                        it[Locations.longitude],
                        it[Locations.timestamp]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, location: ExposedLocations) {
        dbQuery {
            Locations.update({ Locations.id eq id }) {
                it[deviceId] = EntityID(location.deviceId, Locations)
                it[latitude] = location.latitude
                it[longitude] = location.longitude
                it[timestamp] = location.timestamp
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Locations.deleteWhere { Locations.id eq id }
        }
    }
}