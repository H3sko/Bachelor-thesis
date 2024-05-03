package com.example.service

import com.example.models.ExposedUsers
import com.example.models.PasswordChangeRequest
import com.example.models.Users
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

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
                    it[Users.username],
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
            .where { (Users.username eq user.username) and (Users.password eq user.password) }
            .count().toInt() > 0
    }


    suspend fun exists(user: ExposedUsers): Boolean = dbQuery {
        Users.selectAll()
            .where { (Users.username eq user.username) and (Users.password eq user.password) }
            .count().toInt() > 0
    }


    suspend fun getUser(username: String): ExposedUsers? {
        return dbQuery {
            Users.selectAll().where { Users.username eq username }
                .map {
                    ExposedUsers(
                        it[Users.username],
                        it[Users.password]
                    )
                }.singleOrNull()
        }
    }


    suspend fun getUserId(username: String): Int? {
        return dbQuery {
            Users.selectAll().where { Users.username eq username }
                .singleOrNull()
                ?.get(Users.id)
                ?.value
        }
    }



    suspend fun create(user: ExposedUsers): Int = dbQuery {
        Users.insertAndGetId {
            it[username] = user.username
            it[password] = user.password
        }.value
    }


    suspend fun read(id: Int): ExposedUsers? {
        return dbQuery {
            Users.selectAll().where { Users.id eq id }
                .map { ExposedUsers(
                        it[Users.username],
                        it[Users.password]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(payload: PasswordChangeRequest) {
        dbQuery {
            Users.update({ (Users.username eq payload.username) and (Users.password eq payload.oldPassword) }) {
                it[username] = payload.username
                it[password] = payload.newPassword
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
