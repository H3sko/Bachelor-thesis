package bachelorThesis.app.data.model.json

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class UserJson(
    @SerializedName("username") val username: String?,
    @SerializedName("password") val password: String?
)