package com.example.utils.libs

import com.example.models.DeviceCredentials
import com.example.utils.ITEMS_DATA_PATH
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun fetchDataFromFile(filePath: String): List<Map<String, Any>> {
    val file = File(filePath)

    if (!file.exists()) {
        throw IllegalArgumentException("File not found: $filePath")
    }

    val fileContent = file.readText()
    val objectMapper = jacksonObjectMapper()

    return objectMapper.readValue(fileContent)
}

fun findSerialNumberByEntry(list: List<Map<String, Any>>, entryName: String, entryOwner: String): String? {
    for (item in list) {
        val name = item["name"] as? String
        val owner = item["owner"] as? String
        val serialNumber = item["serialNumber"] as? String

        if (name == entryName && owner == entryOwner) {
            return serialNumber
        }
    }
    return null
}

fun exists(deviceCredentials: DeviceCredentials): String? {
    val itemsData = fetchDataFromFile(ITEMS_DATA_PATH)
    val serialNumber = findSerialNumberByEntry(itemsData, deviceCredentials.name, deviceCredentials.owner)

    return serialNumber
}

fun formatTimestamp(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(instant)
}
