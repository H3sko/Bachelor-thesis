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
            .where { (Users.loginEmail eq user.loginEmail) and (Users.hashedPassword eq user.hashedPassword) }
            .count() > 0
    }

    suspend fun exists(user: ExposedUsers): Boolean = dbQuery {
        Users.selectAll().where { Users.loginEmail eq user.loginEmail }
            .count() > 0
    }


    suspend fun create(user: ExposedUsers): Int = dbQuery {
        Users.insert {
            it[loginEmail] = user.loginEmail
            it[hashedPassword] = user.hashedPassword
        }[Users.id].value
    }

    suspend fun read(id: Int): ExposedUsers? {
        return dbQuery {
            Users.selectAll().where { Users.id eq id }
                .map { ExposedUsers(
                        it[Users.loginEmail],
                        it[Users.hashedPassword]
                    )
                }.singleOrNull()
        }
    }

    suspend fun update(id: Int, user: ExposedUsers) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[loginEmail] = user.loginEmail
                it[hashedPassword] = user.hashedPassword
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }
}
