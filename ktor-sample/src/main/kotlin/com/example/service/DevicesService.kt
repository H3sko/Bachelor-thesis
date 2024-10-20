package com.example.service

import com.example.models.ExposedDeviceResponse
import com.example.models.ExposedDevices

interface IDeviceService {
   suspend fun create(device: ExposedDevices): Int
   suspend fun delete(serialNumber: String): Boolean
   suspend fun deleteAllDevices()
   suspend fun deleteAllDevices(userId: Int): Boolean
   suspend fun deleteDevice(id: Int): Boolean
   suspend fun getAll(userId: Int): List<ExposedDeviceResponse>
   suspend fun getAllDevices(): List<List<String>>
   suspend fun getAllIds(): List<Int>
   suspend fun getAllIdsAndSerialNumbers(): List<Pair<Int, String>>
   suspend fun inDatabase(id: Int): Boolean
   suspend fun inDatabaseByName(deviceName: String): Boolean
   suspend fun inDatabaseBySerialNumber(serialNumber: String): Boolean
   suspend fun read(serialNumber: String): ExposedDevices?
   suspend fun readById(id: Int): ExposedDevices?
}

