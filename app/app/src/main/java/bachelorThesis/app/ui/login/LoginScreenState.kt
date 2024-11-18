package bachelorThesis.app.ui.login

data class LoginScreenState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
    val username: String = "",
    val password: String = ""
)