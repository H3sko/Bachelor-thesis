package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import com.example.data.*


class DeviceService {
    init {
        transaction {
            SchemaUtils.create(Devices)
        }
    }

    suspend fun create(device: ExposedDevices): Int = dbQuery {
        Devices.insert {
            it[userId] = device.userId
            it[type] = device.type
        }[Devices.id].value
    }

    suspend fun read(id: Int): ExposedDevices? {
        return dbQuery {
            Devices.selectAll().where { Devices.id eq id }
                .map {
                    ExposedDevices(
                        it[Devices.userId].value,
                        it[Devices.type]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, device: ExposedDevices) {
        dbQuery {
            Devices.update({ Devices.id eq id }) {
                it[userId] = device.userId
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
