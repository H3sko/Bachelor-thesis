package bachelorThesis.app.data.model.dto

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TokenDto(
    @SerializedName("token") val token: String?
)
