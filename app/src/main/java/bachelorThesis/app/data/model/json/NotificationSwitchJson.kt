package bachelorThesis.app.data.model.json

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class NotificationSwitchJson(
    @SerializedName("activeNotification") val activeNotification: Boolean?
)
