package com.example.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*

class LocationsService {
    init {
        transaction {
            SchemaUtils.create(Locations)
        }
    }

    suspend fun create(location: ExposedLocations): Int = dbQuery {
        Locations.insert {
            it[deviceId] = location.deviceId
            it[latitude] = location.latitude
            it[longitude] = location.longitude
            it[timestamp] = location.timestamp
        }[Locations.id].value
    }

    suspend fun read(id: Int): ExposedLocations? {
        return dbQuery {
            Locations.selectAll().where { Locations.id eq id }
                .map {
                    ExposedLocations(
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
                it[deviceId] = location.deviceId
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
