package com.example.service


interface IOnlineUsers {
    // TODO: OnlineUsers
    suspend fun getAllOnlineUsers(): List<Pair<Int, String>>
    suspend fun getTokenByUserId(userId: Int): String?
    suspend fun removeUserOnline(userId: Int): Boolean
    suspend fun updateToken(userId: Int, newToken: String): Boolean
//    suspend fun clearExpiredSessions()
}