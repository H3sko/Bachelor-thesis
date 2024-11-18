package bachelorThesis.app.data.model.json

import androidx.annotation.Keep

@Keep
data class DeviceCredentialsJson(
    val name: String,
    val owner: String
)
