package com.example.quartz.jobs
import com.example.data.service.DeviceService
import com.example.data.service.OnlineUserService
import com.example.models.ExposedDeviceResponse
import com.example.models.ExposedGeofenceVertices
import com.example.models.ExposedLocations
import com.example.models.GeofenceNotification
import com.example.service.GeofenceService
import com.example.service.GeofenceVerticesService
import com.example.service.LocationsService
import com.example.utils.libs.isLocationInsidePolygon
import com.example.utils.libs.sendGeofenceNotification
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DataAlterJob : Job {

    override fun execute(context: JobExecutionContext?) {
        val deviceService = DeviceService()
        val locationsService = LocationsService()
        val geofenceService = GeofenceService()
        val geofenceVerticesService = GeofenceVerticesService()
        val onlineUserService = OnlineUserService()

        // Fetch all device IDs
        val devices: List<ExposedDeviceResponse> = runBlocking {
            deviceService.getAllDevices()
        }

        for (device in devices) {
            runBlocking {
                // Fetch the latest location for each device
                val latestLocation = locationsService.getLatest(device.id)

                val newLocation: ExposedLocations

                if (latestLocation == null) {
                    // If no latest location exists, use the default location
                    newLocation = ExposedLocations(
                        deviceId = device.id,
                        latitude = 49.210060,
                        longitude = 16.599250,
                        timestamp = getCurrentTimestamp()
                    )
                } else {
                    // Generate new random location around the latest one
                    val (newLat, newLong) = getRandomLocation(latestLocation.latitude, latestLocation.longitude)

                    newLocation = ExposedLocations(
                        deviceId = device.id,
                        latitude = newLat,
                        longitude = newLong,
                        timestamp = getCurrentTimestamp()
                    )
                }

                locationsService.create(newLocation)


                // Firebase notification
//  TODO: otestovat
                val deviceGeofenceId: Int? = geofenceService.getGeofence(device.id)

                if (deviceGeofenceId != null) {
                    val deviceGeofenceVertices: List<ExposedGeofenceVertices> = geofenceVerticesService.getAll(deviceGeofenceId)

                    if (deviceGeofenceVertices.isNotEmpty()) {
                        val isInsideGeofence = isLocationInsidePolygon(newLocation, deviceGeofenceVertices)

                        if (!isInsideGeofence) {
                            val onlineUser: Pair<String, Boolean>? = onlineUserService.getOnlineUserByUserId(device.userId)
                            if (onlineUser != null && onlineUser.second) {
                                sendGeofenceNotification(onlineUser.first, GeofenceNotification(title = "Warning", deviceId = device.id, deviceName = device.name))
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper function to get the current timestamp in the desired format
    private fun getCurrentTimestamp(): String {
        val now = Instant.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.systemDefault())
        return formatter.format(now)
    }

    // Helper function to generate a random latitude and longitude around the latest location
    private fun getRandomLocation(lat: Double, long: Double): Pair<Double, Double> {
        val random = java.util.Random()

        // Generate a random move between -50 to +50 meters (~0.00045 degrees latitude/longitude)
        val latMove = (random.nextDouble() * 100 - 50) * 0.00000899322 // Convert meters to degrees
        val longMove = (random.nextDouble() * 100 - 50) * 0.00000899322

        val newLat = lat + latMove
        val newLong = long + longMove

        return Pair(newLat, newLong)
    }
}
