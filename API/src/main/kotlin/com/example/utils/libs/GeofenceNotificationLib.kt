package com.example.utils.libs

import com.example.data.service.OnlineUserService
import com.example.models.ExposedDeviceResponse
import com.example.models.ExposedGeofenceVertices
import com.example.models.ExposedLocations
import com.example.models.GeofenceNotification
import com.example.service.GeofenceService
import com.example.service.GeofenceVerticesService
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import java.lang.Math.toRadians
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.sin
import kotlin.math.tan


fun isLocationInsidePolygon(location: ExposedLocations, vertices: List<ExposedGeofenceVertices>): Boolean {
    // Convert location to LatLng
    val point = LatLng(location.latitude, location.longitude)

    // Convert vertices to LatLng list
    val polygonVertices = vertices.map { LatLng(it.latitude, it.longitude) }

    // Check if the point is inside the polygon
    return containsLocation(point, polygonVertices, true)
}

data class LatLng(
    val latitude: Double,
    val longitude: Double
)

fun containsLocation(point: LatLng, polygon: List<LatLng>, geodesic: Boolean): Boolean {
    return containsLocation(point.latitude, point.longitude, polygon, geodesic)
}

fun containsLocation(latitude: Double, longitude: Double, polygon: List<LatLng>, geodesic: Boolean): Boolean {
    val size = polygon.size
    if (size == 0) {
        return false
    }
    val lat3: Double = toRadians(latitude)
    val lng3: Double = toRadians(longitude)
    val prev: LatLng = polygon[size - 1]
    var lat1: Double = toRadians(prev.latitude)
    var lng1: Double = toRadians(prev.longitude)
    var nIntersect = 0
    for (point2 in polygon) {
        val dLng3: Double = wrap(lng3 - lng1, -PI, PI)
        // Special case: point equal to vertex is inside.
        if (lat3 == lat1 && dLng3 == 0.0) {
            return true
        }
        val lat2: Double = toRadians(point2.latitude)
        val lng2: Double = toRadians(point2.longitude)
        // Offset longitudes by -lng1.
        if (intersects(lat1, lat2, wrap(lng2 - lng1, -PI, PI), lat3, dLng3, geodesic)) {
            ++nIntersect
        }
        lat1 = lat2
        lng1 = lng2
    }
    return nIntersect and 1 != 0
}

fun wrap(n: Double, min: Double, max: Double): Double {
    return if (n >= min && n < max) n else mod(n - min, max - min) + min
}

fun mod(x: Double, m: Double): Double {
    return (x % m + m) % m
}

private fun intersects(
    lat1: Double, lat2: Double, lng2: Double,
    lat3: Double, lng3: Double, geodesic: Boolean
): Boolean {
    // Both ends on the same side of lng3.
    if (lng3 >= 0 && lng3 >= lng2 || lng3 < 0 && lng3 < lng2) {
        return false
    }
    // Point is South Pole.
    if (lat3 <= -PI / 2) {
        return false
    }
    // Any segment end is a pole.
    if (lat1 <= -PI / 2 || lat2 <= -PI / 2 || lat1 >= PI / 2 || lat2 >= PI / 2) {
        return false
    }
    if (lng2 <= -PI) {
        return false
    }
    val linearLat = (lat1 * (lng2 - lng3) + lat2 * lng3) / lng2
    // Northern hemisphere and point under lat-lng line.
    if (lat1 >= 0 && lat2 >= 0 && lat3 < linearLat) {
        return false
    }
    // Southern hemisphere and point above lat-lng line.
    if (lat1 <= 0 && lat2 <= 0 && lat3 >= linearLat) {
        return true
    }
    // North Pole.
    if (lat3 >= PI / 2) {
        return true
    }
    // Compare lat3 with latitude on the GC/Rhumb segment corresponding to lng3.
    // Compare through a strictly-increasing function (tan() or mercator()) as convenient.
    return if (geodesic) tan(lat3) >= tanLatGC(lat1, lat2, lng2, lng3) else mercator(lat3) >= mercatorLatRhumb(
        lat1,
        lat2,
        lng2,
        lng3
    )
}

private fun mercatorLatRhumb(lat1: Double, lat2: Double, lng2: Double, lng3: Double): Double {
    return (mercator(lat1) * (lng2 - lng3) + mercator(lat2) * lng3) / lng2
}

fun mercator(latRad: Double): Double {
    return ln(tan(PI/4 + latRad/2))
}

private fun tanLatGC(lat1: Double, lat2: Double, lng2: Double, lng3: Double): Double {
    return (tan(lat1) * sin(lng2 - lng3) + tan(lat2) * sin(lng3)) / sin(lng2)
}

suspend fun handleGeofenceNotification(
    device: ExposedDeviceResponse,
    newLocation: ExposedLocations
) {
    val geofenceService = GeofenceService()
    val geofenceVerticesService = GeofenceVerticesService()
    val onlineUserService = OnlineUserService()

    val deviceGeofenceId: Int? = geofenceService.getGeofence(device.id)

    if (deviceGeofenceId != null) {
        val deviceGeofenceVertices: List<ExposedGeofenceVertices> = geofenceVerticesService.getAll(deviceGeofenceId)

        if (deviceGeofenceVertices.isNotEmpty()) {
            val isInsideGeofence = isLocationInsidePolygon(newLocation, deviceGeofenceVertices)

            if (!isInsideGeofence) {
                val onlineUser: Pair<String, Boolean>? = onlineUserService.getOnlineUserByUserId(device.userId)
                if (onlineUser != null && onlineUser.second) {
                    sendGeofenceNotification(
                        onlineUser.first,
                        GeofenceNotification(
                            title = "Warning",
                            deviceId = device.id,
                            deviceName = device.name
                        )
                    )
                }
            }
        }
    }
}

fun sendGeofenceNotification(token: String, geofenceNotification: GeofenceNotification) {
    val message = Message.builder()
        .setToken(token)
        .setNotification(
            Notification.builder()
                .setTitle(geofenceNotification.title)
                .setBody("${geofenceNotification.deviceName} has left its geofence")
                .build()
        )
        .setAndroidConfig(
            AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .build()
        )
        .build()

    FirebaseMessaging.getInstance().send(message)
}