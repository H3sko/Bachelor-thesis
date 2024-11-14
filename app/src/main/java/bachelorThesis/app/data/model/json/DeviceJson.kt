package bachelorThesis.app.data.model.json

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DeviceJson(
    @SerializedName("name") val name: String?,
    @SerializedName("owner") val owner: String?
)
