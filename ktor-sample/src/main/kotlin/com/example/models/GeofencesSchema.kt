package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*

class GeofenceService {
    init {
        transaction {
            SchemaUtils.create(Geofences)
        }
    }

    suspend fun create(geofence: ExposedGeofences): Int = dbQuery {
        Geofences.insert {
            it[deviceId] = geofence.deviceId
        }[Geofences.id].value
    }

    suspend fun read(id: Int): ExposedGeofences? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.id eq id }
                .map { ExposedGeofences(
                    it[Geofences.deviceId].value,
                )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, geofence: ExposedGeofences) {
        dbQuery {
            Geofences.update({ Geofences.id eq id }) {
                it[deviceId] = geofence.deviceId
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Geofences.deleteWhere { Geofences.id.eq(id) }
        }
    }
}