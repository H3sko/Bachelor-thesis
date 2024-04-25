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


    suspend fun getAllUsers(): List<List<String>> {
        return dbQuery {
            Users.selectAll().map {
                    listOf( it[Users.id].toString(),
                    it[Users.loginEmail],
                    it[Users.password])
            }
        }
    }


    suspend fun deleteAllUsers() {
        dbQuery {
            Users.deleteAll()
        }
    }


    suspend fun login(user: ExposedUsers): Boolean = dbQuery {
        Users.selectAll()
            .where { (Users.loginEmail eq user.loginEmail) and (Users.password eq user.password) }
            .count().toInt() > 0
    }


    suspend fun exists(loginEmail: String): ExposedUsers? {
        return dbQuery {
            Users.selectAll().where { Users.loginEmail eq loginEmail }
                .map {
                    ExposedUsers(
                        it[Users.loginEmail],
                        it[Users.password]
                    )
                }.singleOrNull()
        }
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
                        it[Users.loginEmail],
                        it[Users.password]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(oldLoginEmail: String, oldPassword: String, newLoginEmail: String, newPassword: String) {
        dbQuery {
            Users.update({ ( Users.loginEmail eq oldLoginEmail ) and ( Users.password eq oldPassword )}) {
                it[loginEmail] = newLoginEmail
                it[password] = newPassword
            }
        }
    }


    suspend fun delete(id: Int): Boolean {
        return dbQuery {
            val deletedUsers = Users.deleteWhere { Users.id eq id }
            deletedUsers > 0
        }
    }
}
