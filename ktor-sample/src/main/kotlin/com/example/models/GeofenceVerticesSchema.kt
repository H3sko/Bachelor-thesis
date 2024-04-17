package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*
import org.jetbrains.exposed.dao.id.EntityID


class GeofenceVerticesService {
    init {
        transaction {
            SchemaUtils.create(GeofenceVertices)
        }
    }

    suspend fun getAll(geofenceId: Int): List<ExposedGeofenceVertices> {
        return dbQuery {
            GeofenceVertices.selectAll().where { GeofenceVertices.geofenceId eq geofenceId }
                .map {
                    ExposedGeofenceVertices(
                        it[GeofenceVertices.id].value,
                        it[GeofenceVertices.geofenceId].value,
                        it[GeofenceVertices.latitude],
                        it[GeofenceVertices.longitude]
                    )
                }
        }
    }


    suspend fun create(geofenceId: Int, latitude: String, longitude: String): Int = dbQuery {
        GeofenceVertices.insertAndGetId {
            it[GeofenceVertices.geofenceId] = EntityID(geofenceId, GeofenceVertices)
            it[GeofenceVertices.latitude] = latitude
            it[GeofenceVertices.longitude] = longitude
        }.value
    }


    suspend fun read(id: Int): ExposedGeofenceVertices? {
        return dbQuery {
            GeofenceVertices.selectAll().where { GeofenceVertices.id eq id }
                .map { ExposedGeofenceVertices(
                    it[GeofenceVertices.id].value,
                    it[GeofenceVertices.geofenceId].value,
                    it[GeofenceVertices.latitude],
                    it[GeofenceVertices.longitude]
                )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, geofencevertex: ExposedGeofenceVertices) {
        dbQuery {
            GeofenceVertices.update({ GeofenceVertices.id eq id }) {
                it[geofenceId] = EntityID(geofencevertex.geofenceId, GeofenceVertices)
                it[latitude] = geofencevertex.latitude
                it[longitude] = geofencevertex.longitude
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            GeofenceVertices.deleteWhere { GeofenceVertices.id.eq(id) }
        }
    }
}