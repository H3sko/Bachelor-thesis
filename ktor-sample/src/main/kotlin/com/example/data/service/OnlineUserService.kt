package com.example.data.service

import com.example.models.ExposedOnlineUser
import com.example.models.OnlineUsers
import com.example.service.IOnlineUsers
import com.example.service.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction


class OnlineUserService : IOnlineUsers {
    init {
        transaction {
            SchemaUtils.create(OnlineUsers)
        }
    }

    override suspend fun create(exposedOnlineUser: ExposedOnlineUser): Int = dbQuery {
        OnlineUsers.insertAndGetId {
            it[userId] = exposedOnlineUser.userId
            it[token] = exposedOnlineUser.token
            it[activeNotification] = exposedOnlineUser.activeNotification
        }.value
    }

    override suspend fun deleteAllOnlineUsers() {
        dbQuery {
            OnlineUsers.deleteAll()
        }
    }

    override suspend fun getAllOnlineUsers(): List<List<String>> {
        return dbQuery {
            OnlineUsers.selectAll().map{
                listOf(
                    it[OnlineUsers.userId].toString(),
                    it[OnlineUsers.token],
                    it[OnlineUsers.activeNotification].toString()
                )
            }
        }
    }

    override suspend fun getOnlineUserByUserId(userId: Int): Pair<String, Boolean>? {
        return dbQuery { OnlineUsers.selectAll().where { OnlineUsers.userId eq userId }
            .map { Pair(it[OnlineUsers.token], it[OnlineUsers.activeNotification]) }
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

    override suspend fun switchNotification(userId: Int, newState: Boolean): Boolean {
        return dbQuery {
            val updatedTokens = OnlineUsers.update({ OnlineUsers.userId eq userId }) { it[activeNotification] = newState }
            updatedTokens > 0
        }
    }

    override suspend fun getNotificationStatus(userId: Int): Boolean? {
        return dbQuery {
            OnlineUsers.selectAll().where { OnlineUsers.userId eq userId }
                .map { it[OnlineUsers.activeNotification] }
                .singleOrNull()
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

   