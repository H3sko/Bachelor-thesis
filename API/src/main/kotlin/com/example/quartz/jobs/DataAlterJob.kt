package com.example.quartz.jobs
import com.example.data.service.DeviceService
import com.example.models.ExposedDeviceResponse
import com.example.models.ExposedLocations
import com.example.service.LocationsService
import com.example.utils.libs.getCurrentTimestamp
import com.example.utils.libs.getRandomLocation
import com.example.utils.libs.handleGeofenceNotification
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext

class DataAlterJob : Job {

    override fun execute(context: JobExecutionContext?) {
        val deviceService = DeviceService()
        val locationsService = LocationsService()

        // Fetch all device IDs
        val devices: List<ExposedDeviceResponse> = runBlocking {
            deviceService.getAllDevices()
        }

        for (device in devices) {
            runBlocking {
                // Fetch the latest location for each device
                val latestLocation = locationsService.getLatest(device.id)

                val newLocation: ExposedLocations = if (latestLocation == null) {
                    // If no latest location exists, use the default location
                    ExposedLocations(
                        deviceId = device.id,
                        latitude = 49.210060,
                        longitude = 16.599250,
                        timestamp = getCurrentTimestamp()
                    )
                } else {
                    // Generate new random location around the latest one
                    val (newLat, newLong) = getRandomLocation(latestLocation.latitude, latestLocation.longitude)

                    ExposedLocations(
                        deviceId = device.id,
                        latitude = newLat,
                        longitude = newLong,
                        timestamp = getCurrentTimestamp()
                    )
                }

                locationsService.create(newLocation)

                // Firebase
                handleGeofenceNotification(
                    device,
                    newLocation
                )
            }
        }
    }
}
