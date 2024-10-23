package com.example.service

import com.example.models.ExposedLocations
import com.example.models.LocationWithId
import com.example.models.Locations
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


class LocationsService : ILocationsService {
    init {
        transaction {
            SchemaUtils.create(Locations)
        }
    }

    override suspend fun create(location: ExposedLocations): Int = dbQuery {
        Locations.insertAndGetId {
            it[deviceId] = location.deviceId
            it[latitude] = location.latitude
            it[longitude] = location.longitude
            it[timestamp] = LocalDateTime.parse(location.timestamp)
        }.value
    }

    override suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedLocations = Locations.deleteWhere { Locations.id eq id }
            deletedLocations > 0
        }
    }

    override suspend fun deleteAll() {
        dbQuery {
            Locations.deleteAll()
        }
    }

    override suspend fun deleteAll(deviceId: Int): Boolean {
        return dbQuery {
            val deletedRowCount = Locations.deleteWhere { Locations.deviceId eq deviceId }
            deletedRowCount > 0
        }
    }

    override suspend fun exists(location: ExposedLocations): Boolean {
        return dbQuery {
            Locations.selectAll().where {
                (Locations.deviceId eq location.deviceId) and
                        (Locations.latitude eq location.latitude) and
                        (Locations.longitude eq location.longitude) and
                        (Locations.timestamp eq LocalDateTime.parse(location.timestamp))
            }.count() > 0
        }
    }

    override suspend fun getAll(): List<List<String>> {
        return dbQuery {
            Locations.selectAll()
                .map {
                    listOf(
                        it[Locations.id].toString(),
                        it[Locations.deviceId].toString(),
                        it[Locations.latitude].toString(),
                        it[Locations.longitude].toString(),
                        it[Locations.timestamp].toString()
                    )
                }
        }
    }

    override suspend fun getAll(deviceId: Int, limit: Int): List<LocationWithId> {
        return dbQuery {
            Locations.selectAll()
                .where { Locations.deviceId eq deviceId }
                .orderBy(column = Locations.timestamp, order = SortOrder.DESC)
                .limit(limit)
                .map {
                    LocationWithId(
                        it[Locations.id].value,
                        it[Locations.deviceId].value,
                        it[Locations.latitude],
                        it[Locations.longitude],
                        it[Locations.timestamp].toString()
                    )
                }
        }
    }

    override suspend fun getLatest(deviceId: Int): ExposedLocations? {
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

    override suspend fun read(id: Int): ExposedLocations? {
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
}

