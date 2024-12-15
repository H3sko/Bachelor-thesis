package com.example.utils.libs

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getCurrentTimestamp(): String {
    val now = Instant.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.systemDefault())
    return formatter.format(now)
}

fun getRandomLocation(lat: Double, long: Double): Pair<Double, Double> {
    val random = java.util.Random()

    // Generate a random move between -50 to +50 meters (~0.00045 degrees latitude/longitude)
    val latMove = (random.nextDouble() * 100 - 50) * 0.00000899322 // Convert meters to degrees
    val longMove = (random.nextDouble() * 100 - 50) * 0.00000899322

    val newLat = lat + latMove
    val newLong = long + longMove

    return Pair(newLat, newLong)
}
   