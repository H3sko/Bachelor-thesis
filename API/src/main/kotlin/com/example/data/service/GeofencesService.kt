package com.example.service

import com.example.models.ExposedGeofences
import com.example.models.Geofences
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class GeofenceService : IGeofenceService {
    init {
        transaction {
            SchemaUtils.create(Geofences)
        }
    }

    override suspend fun create(geofence: ExposedGeofences): Int = dbQuery {
        Geofences.insertAndGetId {
            it[deviceId] = geofence.deviceId
        }.value
    }


    override suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedGeofences = Geofences.deleteWhere { Geofences.id eq id }
            deletedGeofences > 0
        }
    }

    override suspend fun deleteAll() {
        dbQuery {
            Geofences.deleteAll()
        }
    }

    override suspend fun exists(geofence: ExposedGeofences): Boolean {
        return dbQuery {
            Geofences.selectAll()
                .where { Geofences.deviceId eq (geofence.deviceId) }
                .count() > 0
        }
    }

    override suspend fun getAll(): List<List<String>> {
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

    override suspend fun getGeofence(deviceId: Int): Int? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.deviceId eq deviceId }
                .map { it[Geofences.id] }
                .singleOrNull()
                ?.value
        }
    }

    override suspend fun read(id: Int): ExposedGeofences? {
        return dbQuery {
            Geofences.selectAll().where { Geofences.id eq id }
                .map { ExposedGeofences(
                    it[Geofences.deviceId].value
                )
                }.singleOrNull()
        }
    }

    override suspend fun removeGeofence(deviceId: Int): Boolean {
        return dbQuery {
            val deletedGeofences = Geofences.deleteWhere { Geofences.deviceId eq deviceId }
            deletedGeofences > 0
        }
    }
}
