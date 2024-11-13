package com.example.service

import com.example.models.ExposedGeofenceVertices

interface IGeofenceVerticesService {
   suspend fun create(geofenceVertex: ExposedGeofenceVertices): Int
   suspend fun delete(id: Int): Boolean
   suspend fun deleteAll()
   suspend fun deleteAll(geofenceId: Int): Boolean
   suspend fun exists(geofenceVertex: ExposedGeofenceVertices): Boolean
   suspend fun getAll(): List<List<String>>
   suspend fun getAll(geofenceId: Int): List<ExposedGeofenceVertices>
   suspend fun read(id: Int): ExposedGeofenceVertices?
}

