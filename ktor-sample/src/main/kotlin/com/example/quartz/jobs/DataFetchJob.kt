package com.example.quartz.jobs

import com.example.models.ExposedLocations
import com.example.service.DeviceService
import com.example.service.LocationsService
import com.example.utils.ITEMS_DATA_PATH
import com.example.utils.libs.fetchDataFromFile
import kotlinx.coroutines.runBlocking
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DataFetchJob : Job {

   override fun execute(context: JobExecutionContext?) {
      val deviceService = DeviceService()
      val locationsService = LocationsService()

      val filePath = ITEMS_DATA_PATH
      val itemsData: List<Map<String, Any>> = fetchDataFromFile(filePath)

      val idsAndSerialNumbers = runBlocking {
         deviceService.getAllIdsAndSerialNumbers()
      }

      for (item in itemsData) {
         for (pair in idsAndSerialNumbers) {
            val serialNumber = item["serialNumber"] as? String
            if (serialNumber == pair.second) {
               val latitude = (item["location"] as? Map<*, *>)?.get("latitude") as? Double
               val longitude = (item["location"] as? Map<*, *>)?.get("longitude") as? Double
               val timestamp = (item["location"] as? Map<*, *>)?.get("timeStamp") as? Long

               if (latitude != null && longitude != null && timestamp != null) {
                  val formattedTimestamp = formatTimestamp(timestamp)
                  runBlocking {
                     val location = ExposedLocations(pair.first, latitude, longitude, formattedTimestamp)
                     val lastLocation = locationsService.getLatest(pair.first)

                     if (lastLocation == null || location != lastLocation) {
                        locationsService.create(location)
                     }
                  }
               }
            }
         }
      }
   }

   private fun formatTimestamp(timestamp: Long): String {
      val instant = Instant.ofEpochMilli(timestamp)
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(ZoneId.systemDefault())
      return formatter.format(instant)
   }
}