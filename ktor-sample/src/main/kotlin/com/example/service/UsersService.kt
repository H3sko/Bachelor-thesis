package com.example.service

import com.example.models.ExposedUsers
import com.example.models.PasswordChangeRequest
import com.example.models.UserCredentialsRequest

interface IUserService {
   suspend fun create(user: ExposedUsers): Int
   suspend fun delete(id: Int): Boolean
   suspend fun deleteAllUsers()
   suspend fun exists(user: UserCredentialsRequest): Boolean
   suspend fun getAllUsers(): List<List<String>>
   suspend fun getUser(username: String): ExposedUsers?
   suspend fun getUserId(username: String): Int?
   suspend fun login(user: ExposedUsers): Boolean
   suspend fun read(id: Int): ExposedUsers?
   suspend fun update(payload: PasswordChangeRequest)
}

