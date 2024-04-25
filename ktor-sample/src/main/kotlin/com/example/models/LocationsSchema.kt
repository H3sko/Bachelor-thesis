package com.example.models

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalDateTime

class LocationsService {
    init {
        transaction {
            SchemaUtils.create(Locations)
        }
    }


    suspend fun getAll(): List<List<String>> {
        return dbQuery {
            Locations.selectAll()
                .map {
                    listOf(
                        it[Locations.id].toString(),
                        it[Locations.deviceId].toString(),
                        it[Locations.latitude],
                        it[Locations.longitude],
                        it[Locations.timestamp].toString()
                    )
                }
        }
    }


    suspend fun deleteAll() {
        dbQuery {
            Locations.deleteAll()
        }
    }


    suspend fun deleteAll(deviceId: Int): Boolean {
        return dbQuery {
            val deletedRowCount = Locations.deleteWhere { Locations.deviceId eq deviceId }
            deletedRowCount > 0
        }
    }


    suspend fun exists(location: ExposedLocations): Boolean {
        return dbQuery {
            Locations.selectAll().where {
                (Locations.deviceId eq location.deviceId) and
                        (Locations.latitude eq location.latitude) and
                        (Locations.longitude eq location.longitude) and
                        (Locations.timestamp eq LocalDateTime.parse(location.timestamp))
            }.count() > 0
        }
    }




    suspend fun getLatest(deviceId: Int): ExposedLocations? {
        return dbQuery {
            Locations.selectAll().where { Locations.deviceId eq deviceId }
                .maxByOrNull { it[Locations.timestamp] }
                ?.let {
                    ExposedLocations(
                        it[Locations.deviceId].value,
                        it[Locations.latitude],
                        it[Locations.longitude],
                        it[Locations.timestamp].toString()
                    )
                }
        }
    }


    suspend fun create(location: ExposedLocations): Int = dbQuery {
        Locations.insertAndGetId {
            it[deviceId] = EntityID(location.deviceId, Locations)
            it[latitude] = location.latitude
            it[longitude] = location.longitude
            it[timestamp] = LocalDateTime.parse(location.timestamp)
        }.value
    }


    suspend fun read(id: Int): ExposedLocations? {
        return dbQuery {
            Locations.selectAll().where { Locations.id eq id }
                .map {
                    ExposedLocations(
                        it[Locations.deviceId].value,
                        it[Locations.latitude],
                        it[Locations.longitude],
                        it[Locations.timestamp].toString()
                    )
                }.singleOrNull()
        }
    }


    suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedLocations = Locations.deleteWhere { Locations.id eq id }
            deletedLocations > 0
        }
    }
}
