package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*
import org.jetbrains.exposed.dao.id.EntityID

class GeofenceService {
    init {
        transaction {
            SchemaUtils.create(Geofences)
        }
    }

    suspend fun getGeofence(deviceId: Int): Int? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.deviceId eq deviceId }
                .map { it[Geofences.id].value }
                .singleOrNull()
        }
    }

    suspend fun create(deviceId: Int): Int = dbQuery {
        Geofences.insertAndGetId {
            it[GeofenceVertices.geofenceId] = EntityID(deviceId, Geofences)
        }.value
    }



    suspend fun read(id: Int): ExposedGeofences? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.id eq id }
                .map { ExposedGeofences(
                    it[Geofences.id].value,
                    it[Geofences.deviceId].value,
                )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, geofence: ExposedGeofences) {
        dbQuery {
            Geofences.update({ Geofences.id eq id }) {
                it[deviceId] = EntityID(geofence.deviceId, Geofences)
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Geofences.deleteWhere { Geofences.id.eq(id) }
        }
    }
}