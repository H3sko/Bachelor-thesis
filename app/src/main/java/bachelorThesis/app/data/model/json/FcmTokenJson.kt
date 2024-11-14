package bachelorThesis.app.data.model.json

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FcmTokenJson(
    @SerializedName("token") val token: String?,
    @SerializedName("activeNotification") val activeNotification: Boolean?
)
