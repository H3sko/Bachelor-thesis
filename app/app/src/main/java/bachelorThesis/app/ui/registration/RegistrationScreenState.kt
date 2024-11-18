package bachelorThesis.app.ui.registration

data class RegistrationScreenState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val username: String = "",
    val password: String = ""
)