package com.example.service

import com.example.models.ExposedGeofenceVertices
import com.example.models.GeofenceVertices
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class GeofenceVerticesService {
    init {
        transaction {
            SchemaUtils.create(GeofenceVertices)
        }
    }

    suspend fun getAll(): List<List<String>> {
        return dbQuery {
            GeofenceVertices.selectAll()
                .map {
                    listOf(
                        it[GeofenceVertices.id].toString(),
                        it[GeofenceVertices.geofenceId].toString(),
                        it[GeofenceVertices.latitude],
                        it[GeofenceVertices.longitude],
                    )
                }
        }
    }


    suspend fun getAll(geofenceId: Int): List<ExposedGeofenceVertices> {
        return dbQuery {
            GeofenceVertices.selectAll().where { GeofenceVertices.geofenceId eq geofenceId }
                .map {
                    ExposedGeofenceVertices(
                        it[GeofenceVertices.geofenceId].value,
                        it[GeofenceVertices.latitude],
                        it[GeofenceVertices.longitude]
                    )
                }
        }
    }


    suspend fun deleteAll() {
        dbQuery {
            GeofenceVertices.deleteAll()
        }
    }


    suspend fun deleteAll(geofenceId: Int): Boolean {
        return dbQuery {
            val deletedRowCount = GeofenceVertices.deleteWhere { GeofenceVertices.geofenceId eq geofenceId }
            deletedRowCount > 0
        }
    }


    suspend fun exists(geofenceVertex: ExposedGeofenceVertices): Boolean {
        return dbQuery {
            GeofenceVertices.selectAll()
                .where { (GeofenceVertices.geofenceId eq geofenceVertex.geofenceId) and (GeofenceVertices.latitude eq geofenceVertex.latitude) and (GeofenceVertices.longitude eq geofenceVertex.longitude) }
                .count() > 0
        }
    }


    suspend fun create(geofenceVertex: ExposedGeofenceVertices): Int = dbQuery {
        GeofenceVertices.insertAndGetId {
            it[geofenceId] = EntityID(geofenceVertex.geofenceId, GeofenceVertices)
            it[latitude] = geofenceVertex.latitude
            it[longitude] = geofenceVertex.longitude
        }.value
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


    suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedGeofenceVertices = GeofenceVertices.deleteWhere { GeofenceVertices.id eq id }
            deletedGeofenceVertices > 0
        }
    }
}