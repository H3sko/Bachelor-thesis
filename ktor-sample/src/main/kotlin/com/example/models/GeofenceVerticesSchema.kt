package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*


class GeofenceVerticesService {
    init {
        transaction {
            SchemaUtils.create(GeofenceVertices)
        }
    }

    suspend fun create(geofencevertex: ExposedGeofenceVertices): Int = dbQuery {
        GeofenceVertices.insert {
            it[geofenceId] = geofencevertex.geofenceId
            it[latitude] = geofencevertex.latitude
            it[longitude] = geofencevertex.longitude
        }[GeofenceVertices.id].value
    }

    suspend fun read(id: Int): ExposedGeofenceVertices? {
        return dbQuery {
            GeofenceVertices.selectAll().where { GeofenceVertices.id eq id }
                .map { ExposedGeofenceVertices(
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
                it[geofenceId] = geofencevertex.geofenceId
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