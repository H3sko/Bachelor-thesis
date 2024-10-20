package com.example.quartz.jobs
import com.example.data.service.DeviceService
import com.example.models.ExposedLocations
import com.example.service.LocationsService
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

        // Fetch all device IDs
        val deviceIds: List<Int> = runBlocking {
            deviceService.getAllIds()
        }

        for (deviceId in deviceIds) {
            runBlocking {
                // Fetch the latest location for each device
                val latestLocation = locationsService.getLatest(deviceId)

                val newLocation: ExposedLocations

                if (latestLocation == null) {
                    // If no latest location exists, use the default location
                    newLocation = ExposedLocations(
                        deviceId = deviceId,
                        latitude = 49.210060,
                        longitude = 16.599250,
                        timestamp = getCurrentTimestamp()
                    )
                } else {
                    // Generate new random location around the latest one
                    val (newLat, newLong) = getRandomLocation(latestLocation.latitude, latestLocation.longitude)

                    newLocation = ExposedLocations(
                        deviceId = deviceId,
                        latitude = newLat,
                        longitude = newLong,
                        timestamp = getCurrentTimestamp()
                    )
                }

                // Insert the new location into the database
                locationsService.create(newLocation)
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
