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


    suspend fun getAll(): List<List<String>> {
        return dbQuery {
            Geofences.selectAll()
                .map {
                    listOf(
                        it[Geofences.id].toString(),
                        it[Geofences.deviceId].toString()
                    )
                }
        }
    }


    suspend fun deleteAll() {
        dbQuery {
            Geofences.deleteAll()
        }
    }


    suspend fun getGeofence(deviceId: Int): Int? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.deviceId eq deviceId }
                .map { it[Geofences.id].value }
                .singleOrNull()
        }
    }

    suspend fun exists(geofence: ExposedGeofences): Boolean {
        return dbQuery {
            Geofences.selectAll()
                .where { Geofences.deviceId eq geofence.deviceId }
                .count() > 0
        }
    }


    suspend fun create(geofence: ExposedGeofences): Int = dbQuery {
        Geofences.insertAndGetId {
            it[deviceId] = EntityID(geofence.deviceId, Geofences)
        }.value
    }


    suspend fun read(id: Int): ExposedGeofences? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.id eq id }
                .map { ExposedGeofences(
                    it[Geofences.deviceId].value
                )
                }.singleOrNull()
        }
    }


    suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedGeofences = Geofences.deleteWhere { Geofences.id eq id }
            deletedGeofences > 0
        }
    }


    suspend fun removeGeofence(deviceId: Int): Boolean {
        return dbQuery {
            val deletedGeofences = Geofences.deleteWhere { Geofences.deviceId eq deviceId }
            deletedGeofences > 0
        }
    }
}