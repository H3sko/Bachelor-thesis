package bachelorThesis.app.ui.registration

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bachelorThesis.app.common.Resource
import bachelorThesis.app.data.model.json.UserJson
import bachelorThesis.app.domain.useCase.users.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class RegistrationScreenViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {


    private val _state: MutableState<RegistrationScreenState> = mutableStateOf(RegistrationScreenState())
    val state: State<RegistrationScreenState> = _state


     fun register() {
        val userRequest = UserJson(state.value.username, state.value.password)
        registerUseCase(userRequest).onEach { result: Resource<Int> ->
            when (result) {
                is Resource.Loading<*> -> {
                    setLoading(true)
                    setError(null)
                }
                is Resource.Error<*> -> {
                    setLoading(false)
                    when(result.code) {
                        409 -> { setError("This user already exists") }
                        500 -> { setError("Internal Server Error") }
                        -1 -> { setError("Internet connection error") }
                        else -> { setError("An unexpected error occurred") }
                    }
                }
                is Resource.Success<*> -> {
                    _state.value = RegistrationScreenState(success = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun setError(newValue: String?) {
        _state.value = state.value.copy(
            error = newValue
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