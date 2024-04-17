package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*
import org.jetbrains.exposed.dao.id.EntityID


class DeviceService {
    init {
        transaction {
            SchemaUtils.create(Devices)
        }
    }

    suspend fun getAll(userId: Int): List<ExposedDevices> {
        return dbQuery {
            Devices.selectAll().where { Devices.userId eq userId }
                .map {
                    ExposedDevices(
                        it[Devices.id].value,
                        it[Devices.userId].value,
                        it[Devices.type]
                    )
                }
        }
    }

    suspend fun create(userId: Int, type: String): Int = dbQuery {
        Devices.insertAndGetId {
            it[Devices.userId] = EntityID(userId, Devices)
            it[Devices.type] = type
        }.value
    }


    suspend fun read(id: Int): ExposedDevices? {
        return dbQuery {
            Devices.selectAll().where { Devices.id eq id }
                .map {
                    ExposedDevices(
                        it[Devices.id].value,
                        it[Devices.userId].value,
                        it[Devices.type]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, device: ExposedDevices) {
        dbQuery {
            Devices.update({ Devices.id eq id }) {
                it[userId] = EntityID(device.userId, Devices)
                it[type] = device.type
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Devices.deleteWhere { Devices.id eq id }
        }
    }
}
