package com.example.data.service

import com.example.models.OnlineUsers
import com.example.service.IOnlineUsers
import com.example.service.dbQuery
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update


class OnlineUserService : IOnlineUsers {
    init {
        transaction {
            SchemaUtils.create(OnlineUsers)
        }
    }

    override suspend fun getAllOnlineUsers(): List<Pair<Int, String>> {
        return dbQuery {
            OnlineUsers.selectAll().map{
                Pair(it[OnlineUsers.userId].value, it[OnlineUsers.token])
            }
        }
    }

    override suspend fun getTokenByUserId(userId: Int): String? {
        return dbQuery { OnlineUsers.selectAll().where { OnlineUsers.userId eq userId }
            .mapNotNull { it[OnlineUsers.token] }
            .singleOrNull()
        }
    }

    override suspend fun removeUserOnline(userId: Int): Boolean {
        return dbQuery {
            val deletedUsers = dbQuery { OnlineUsers.deleteWhere { OnlineUsers.userId eq userId } }
            deletedUsers > 0
        }
    }

    override suspend fun updateToken(userId: Int, newToken: String): Boolean {
        return dbQuery {
            val updatedTokens = OnlineUsers.update({ OnlineUsers.userId eq userId }) { it[token] = newToken }
            updatedTokens > 0
        }
    }

    // TODO: maybe
//    override suspend fun clearExpiredSessions() {
//        return dbQuery {
//            val expirationThreshold = DateTime.now().minusHours(24) // Customize expiration duration
//            OnlineUsers.deleteWhere { OnlineUsers.timestamp lessEq expirationThreshold }
//        }
//    }
}

   