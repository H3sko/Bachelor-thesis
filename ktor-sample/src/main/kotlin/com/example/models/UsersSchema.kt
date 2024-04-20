package com.example.models

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import com.example.data.*

suspend fun <T> dbQuery(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) { block() }


class UserService {
    init {
        transaction {
            SchemaUtils.create(Users)
        }
    }

    suspend fun login(user: ExposedUsers): Boolean = dbQuery {
        Users.selectAll()
            .where { (Users.loginEmail eq user.loginEmail) and (Users.password eq user.password) }
            .count() > 0
    }

    suspend fun exists(loginEmail: String): Boolean = dbQuery {
        Users.selectAll().where { Users.loginEmail eq loginEmail }
            .count() > 0
    }


    suspend fun create(user: ExposedUsers): Int = dbQuery {
        Users.insertAndGetId {
            it[loginEmail] = user.loginEmail
            it[password] = user.password
        }.value
    }


    suspend fun read(id: Int): ExposedUsers? {
        return dbQuery {
            Users.selectAll().where { Users.id eq id }
                .map { ExposedUsers(
                        it[Users.id].value,
                        it[Users.loginEmail],
                        it[Users.password]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, loginEmail: String, hashedPassword: String) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[Users.loginEmail] = loginEmail
                it[Users.password] = hashedPassword
            }
        }
    }


    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
