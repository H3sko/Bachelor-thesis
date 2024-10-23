package com.example.service

import com.example.models.ExposedLocations
import com.example.models.LocationWithId

interface ILocationsService {
   suspend fun create(location: ExposedLocations): Int
   suspend fun delete(id: Int): Boolean
   suspend fun deleteAll()
   suspend fun deleteAll(deviceId: Int): Boolean
   suspend fun exists(location: ExposedLocations): Boolean
   suspend fun getAll(): List<List<String>>
   suspend fun getAll(deviceId: Int, limit: Int): List<LocationWithId>
   suspend fun getLatest(deviceId: Int): ExposedLocations?
   suspend fun read(id: Int): ExposedLocations?
}

