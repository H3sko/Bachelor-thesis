package bachelorThesis.app.data.model.dto

import androidx.annotation.Keep

@Keep
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val timestamp: String
)
