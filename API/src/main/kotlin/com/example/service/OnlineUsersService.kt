package com.example.service

import com.example.models.ExposedOnlineUser


interface IOnlineUsers {
    suspend fun create(exposedOnlineUser: ExposedOnlineUser): Int
    suspend fun deleteAllOnlineUsers()
    suspend fun getAllOnlineUsers(): List<List<String>>
    suspend fun getOnlineUserByUserId(userId: Int): Pair<String, Boolean>?
    suspend fun removeUserOnline(userId: Int): Boolean
    suspend fun updateToken(userId: Int, newToken: String): Boolean
    suspend fun switchNotification(userId: Int, newState: Boolean): Boolean
    suspend fun getNotificationStatus(userId: Int): Boolean?
//    suspend fun clearExpiredSessions()
}