package com.example.service

import com.example.models.Devices
import com.example.models.ExposedDevices
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class DeviceService {
    init {
        transaction {
            SchemaUtils.create(Devices)
        }
    }


    suspend fun getAllDevices(): List<List<String>> {
        return dbQuery {
            Devices.selectAll()
                .map {
                    listOf(
                        it[Devices.id].toString(),
                        it[Devices.userId].toString(),
                        it[Devices.name]
                    )
                }
        }
    }


    suspend fun deleteAllDevices() {
        dbQuery {
            Devices.deleteAll()
        }
    }


    suspend fun deleteAllDevices(userId: Int): Boolean {
        val deletedRows = dbQuery {
            Devices.deleteWhere { Devices.userId eq userId }
        }
        return deletedRows > 0
    }



    suspend fun exists(device: ExposedDevices): Boolean {
        return dbQuery {
            Devices.selectAll()
                .where { (Devices.userId eq EntityID(device.userId, Devices)) and (Devices.name eq device.name) }
                .count() > 0
        }
    }


    suspend fun getAll(userId: Int): List<ExposedDevices> {
        return dbQuery {
            Devices.selectAll().where { Devices.userId eq userId }
                .map {
                    ExposedDevices(
                        it[Devices.userId].value,
                        it[Devices.name]
                    )
                }
        }
    }

    suspend fun create(device: ExposedDevices): Int = dbQuery {
        Devices.insertAndGetId {
            it[userId] = EntityID(device.userId, Devices)
            it[name] = device.name
        }.value
    }


    suspend fun read(id: Int): ExposedDevices? {
        return dbQuery {
            Devices.selectAll().where { Devices.id eq id }
                .map {
                    ExposedDevices(
                        it[Devices.userId].value,
                        it[Devices.name]
                    )
                }.singleOrNull()
        }
    }


    suspend fun deleteDevice(exposedDevice: ExposedDevices): Boolean {
        return dbQuery {
            val deletedRows = Devices.deleteWhere { (Devices.userId eq exposedDevice.userId) and (Devices.name eq exposedDevice.name) }
            deletedRows > 0
        }
    }


    suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedRows = Devices.deleteWhere { Devices.id eq id }
            deletedRows > 0
        }
    }

}
