package com.example.service

import com.example.models.Devices
import com.example.models.ExposedDeviceResponse
import com.example.models.ExposedDevices
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class DeviceService : IDeviceService {
    init {
        transaction {
            SchemaUtils.create(Devices)
        }
    }

    override suspend fun create(device: ExposedDevices): Int = dbQuery {
        Devices.insertAndGetId {
            it[userId] = device.userId
            it[name] = device.name
            it[serialNumber] = device.serialNumber
        }.value
    }

    override suspend fun delete(serialNumber: String): Boolean {
        return dbQuery {
            val deletedRows = Devices.deleteWhere { Devices.serialNumber eq serialNumber }
            deletedRows > 0
        }
    }


    override suspend fun deleteAllDevices() {
        dbQuery {
            Devices.deleteAll()
        }
    }

    override suspend fun deleteAllDevices(userId: Int): Boolean {
        return dbQuery {
            val deletedRows = Devices.deleteWhere { Devices.userId eq userId }
            deletedRows > 0
        }
    }

    override suspend fun deleteDevice(id: Int): Boolean {
        return dbQuery {
            val deletedRows = Devices.deleteWhere { Devices.id eq id }
            deletedRows > 0
        }
    }

    override suspend fun inDatabase(id: Int): Boolean {
        return dbQuery {
            Devices.selectAll()
                .where { Devices.id eq id}
                .count() > 0
        }
    }

    override suspend fun inDatabaseBySerialNumber(serialNumber: String): Boolean {
        return dbQuery {
            Devices.selectAll()
                .where { Devices.serialNumber eq serialNumber}
                .count() > 0
        }
    }

    override suspend fun getAllDevices(): List<List<String>> {
        return dbQuery {
            Devices.selectAll()
                .map {
                    listOf(
                        it[Devices.id].toString(),
                        it[Devices.userId].toString(),
                        it[Devices.name],
                        it[Devices.serialNumber]
                    )
                }
        }
    }

    override suspend fun getAll(userId: Int): List<ExposedDeviceResponse> {
        return dbQuery {
            Devices.selectAll().where { Devices.userId eq userId }
                .map {
                    ExposedDeviceResponse(
                        it[Devices.id].value,
                        it[Devices.userId].value,
                        it[Devices.name],
                        it[Devices.serialNumber]
                    )
                }
        }
    }


    override suspend fun getAllIdsAndSerialNumbers(): List<Pair<Int, String>> {
        return dbQuery {
            Devices.selectAll().map {
                Pair(it[Devices.id].value, it[Devices.serialNumber])
            }
        }
    }



    override suspend fun read(serialNumber: String): ExposedDevices? {
        return dbQuery {
            Devices.selectAll().where { Devices.serialNumber eq serialNumber }
                .map {
                    ExposedDevices(
                        it[Devices.userId].value,
                        it[Devices.name],
                        it[Devices.serialNumber]
                    )
                }.singleOrNull()
        }
    }

    override suspend fun readById(id: Int): ExposedDevices? {
        return dbQuery {
            Devices.selectAll().where { Devices.id eq id }
                .map {
                    ExposedDevices(
                        it[Devices.userId].value,
                        it[Devices.name],
                        it[Devices.serialNumber]
                    )
                }.singleOrNull()
        }
    }
}

