package bachelorThesis.app.ui.login

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.dto.TokenDto
import bachelorThesis.app.data.model.json.UserJson
import bachelorThesis.app.domain.useCase.dataStore.ClearDataUseCase
import bachelorThesis.app.domain.useCase.dataStore.SetJwtTokenUseCase
import bachelorThesis.app.domain.useCase.users.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val setJwtTokenUseCase: SetJwtTokenUseCase,
    private val clearDataUseCase: ClearDataUseCase
) : ViewModel() {


    private val _state: MutableState<LoginScreenState> = mutableStateOf(LoginScreenState())
    val state: State<LoginScreenState> = _state


    suspend fun logIn() {
        val userRequest = UserJson(state.value.username, state.value.password)
        loginUseCase(userRequest).onEach { result: Resource<TokenDto> ->
            when (result) {
                is Resource.Loading<*> -> {
                    setLoading(true)
                    setError(null)
                }
                is Resource.Error<*> -> {
                    setLoading(false)
                    when(result.code) {
                        401 -> { setError("Wrong Username or Password ") }
                        -1 -> { setError("Internet connection error") }
                        else -> { setError("An unexpected error occurred") }
                    }
                }
                is Resource.Success<*> -> {
                    val token = result.data
                    if (token != null) {
                        if (token.token != null) {
                            setJwtTokenUseCase(token.token)
                            _state.value = LoginScreenState(success = true)
                        } else {
                            setError("Token missing, please try to login again")
                        }
                    } else {
                        setError("Something went wrong, please try to login again")
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun logOut() {
        runBlocking { clearDataUseCase() }
    }

    private fun setError(message: String?) {
        _state.value = state.value.copy(
            error = message
        )
    }

    fun setUsername(newValue: String) {
        _state.value = state.value.copy(
            username = newValue
        )
    }

    fun setPassword(newValue: String) {
        _state.value = state.value.copy(
            password = newValue
        )
    }
    private fun setLoading(newValue: Boolean) {
        _state.value = state.value.copy(
            isLoading = newValue
        )
    }
}