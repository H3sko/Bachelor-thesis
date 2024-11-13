package com.example.service

import com.example.models.ExposedGeofences

interface IGeofenceService {
   suspend fun create(geofence: ExposedGeofences): Int
   suspend fun delete(id: Int): Boolean
   suspend fun deleteAll()
   suspend fun exists(geofence: ExposedGeofences): Boolean
   suspend fun getAll(): List<List<String>>
   suspend fun getGeofence(deviceId: Int): Int?
   suspend fun read(id: Int): ExposedGeofences?
   suspend fun removeGeofence(deviceId: Int): Boolean
}

