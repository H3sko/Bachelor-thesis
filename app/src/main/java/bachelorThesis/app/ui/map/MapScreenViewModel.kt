package bachelorThesis.app.ui.map

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bachelorThesis.app.common.Resource
import bachelorThesis.app.common.defaultZoom
import bachelorThesis.app.data.remote.dto.Device
import bachelorThesis.app.data.remote.dto.DeviceCredentials
import bachelorThesis.app.data.remote.dto.GeofenceVertex
import bachelorThesis.app.data.remote.dto.LocationDto
import bachelorThesis.app.domain.useCase.dataStore.GetJwtTokenUseCase
import bachelorThesis.app.domain.useCase.devices.AddDeviceUseCase
import bachelorThesis.app.domain.useCase.devices.GetDevicesUseCase
import bachelorThesis.app.domain.useCase.devices.RemoveDeviceUseCase
import bachelorThesis.app.domain.useCase.geofences.AddGeofenceUseCase
import bachelorThesis.app.domain.useCase.geofences.GetGeofenceUseCase
import bachelorThesis.app.domain.useCase.geofences.RemoveGeofenceUseCase
import bachelorThesis.app.domain.useCase.locations.GetAllLocationsUseCase
import bachelorThesis.app.domain.useCase.locations.GetLocationUseCase
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

const val TAG: String = "MapsViewModel"

@HiltViewModel
class MapScreenViewModel @Inject constructor(
    private val addDeviceUseCase: AddDeviceUseCase,
    private val getDevicesUseCase: GetDevicesUseCase,
    private val removeDeviceUseCase: RemoveDeviceUseCase,
    private val addGeofenceUseCase: AddGeofenceUseCase,
    private val getGeofenceUseCase: GetGeofenceUseCase,
    private val removeGeofenceUseCase: RemoveGeofenceUseCase,
    private val getAllLocationsUseCase: GetAllLocationsUseCase,
    private val getLocationUseCase: GetLocationUseCase,
    private val getJwtTokenUseCase: GetJwtTokenUseCase,
) : ViewModel() {
    private val _state: MutableState<MapScreenState> = mutableStateOf(MapScreenState())
    val state: State<MapScreenState> = _state


    init {
        initMapScreenViewModel()
    }

    private fun initMapScreenViewModel() {
        getJwtTokenUseCase()
            .onEach { result ->
                if (result.isNotEmpty()) {
                    getDevicesFromDb()
                } else {
                    setError("Please re-login")
                }
            }.launchIn(viewModelScope)
        // TODO: getPeriodicLocation() ?
    }


    private fun getJwtToken() {
        getJwtTokenUseCase()
            .onEach { result ->
                if (result.isEmpty()) {
                    Log.i(TAG, "Token not saved in the dataStore")
                    setError("Something went wrong, please logIn again")
                } else {
                    setToken(result)
                }
            }.launchIn(viewModelScope)
    }

    fun addDeviceToDb() {
        addDeviceUseCase(_state.value.token, DeviceCredentials(state.value.deviceName, state.value.deviceOwner))
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (result.data != null) {
                            addDevice(Device(result.data, state.value.deviceName))
                        } else {
                            setError("Something went wrong, please restart the app to load the devices properly")
                        }
                    }
                    is Resource.Error -> {
                        when(result.code) {
                            400 -> { setError("Credentials cannot be empty") }
                            404 -> { setError("${state.value.deviceName} device doesn't match to the ${state.value.deviceOwner} owner") }
                            409 -> { setError("You have already added this device") }
                            -1 -> { setError("Internet connection error") }
                            else -> { setError("An unexpected error occurred") }
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    private fun getDevicesFromDb() {
        getDevicesUseCase(_state.value.token)
            .onEach { result ->
                when (result) {
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (result.data != null) {
                            setDevices(result.data)
                        } else {
                            setError("Something went wrong, please restart the app to load the devices properly")
                        }
                    }
                    is Resource.Error -> {
                        when(result.code) {
                            400 -> { setError("Something went wrong, please restart the ap") }
                            -1 -> { setError("Internet connection error") }
                            else -> { setError("An unexpected error occurred") }
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun removeDeviceFromDb() {
        val deviceId = _state.value.device?.id
        if (deviceId != null) {
            removeDeviceUseCase(_state.value.token, deviceId.toString())
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            removeDevice(deviceId)
                        }
                        is Resource.Error -> {
                            when(result.code) {
                                409 -> { setError("Device that you are trying to delete, doesn't exist, please reload the app") }
                                -1 -> { setError("Internet connection error") }
                                else -> { setError("An unexpected error occurred") }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            setError("Something went wrong, please try again")
        }
    }

    fun addGeofenceToDb() {
        val deviceId = _state.value.device?.id
        if (deviceId != null) {
            addGeofenceUseCase(
                credentials = _state.value.token,
                deviceId = deviceId.toString(),
                vertices = _state.value.addVertices
            )
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            setDeviceGeofenceVertices(_state.value.addVertices)
                        }
                        is Resource.Error -> {
                            when (result.code) {
                                400 -> {
                                    setError("Geofence needs at least 3 vertices")
                                }
                                401 -> {
                                    setError("Try to re-login")
                                }
                                404 -> {
                                    setError("This device doesn't exist, please try again")
                                }
                                409 -> {
                                    setError("This device already has a geofence")
                                }
                                -1 -> {
                                    setError("Internet connection error")
                                }
                                else -> {
                                    setError("An unexpected error occurred")
                                }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            setError("Something went wrong, please try again")
        }
    }

    fun getGeofenceFromDb() {
        val deviceId = _state.value.device?.id
        if (deviceId != null) {
            getGeofenceUseCase(credentials = _state.value.token, deviceId = deviceId.toString())
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            if (result.data != null) {
                                setDeviceGeofenceVertices(result.data)
                            }
                        }
                        is Resource.Error -> {
                            when (result.code) {
                                400 -> {
                                    setError("This device doesn't exist, please try again")
                                }
                                401 -> {
                                    setError("Try to re-login")
                                }
                                404 -> {
                                    setError("This device doesn't have a geofence")
                                }
                                -1 -> {
                                    setError("Internet connection error")
                                }
                                else -> {
                                    setError("An unexpected error occurred")
                                }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            setError("Something went wrong, please try again")
        }
    }

    fun removeGeofenceFromDb() {
        val deviceId = _state.value.device?.id
        if (deviceId != null) {
            removeGeofenceUseCase(credentials = _state.value.token, deviceId = deviceId.toString())
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            removeGeofence()
                        }
                        is Resource.Error -> {
                            when (result.code) {
                                401 -> {
                                    setError("Try to re-login")
                                }
                                404 -> {
                                    setError("This device doesn't exist, try to re-login")
                                }
                                -1 -> {
                                    setError("Internet connection error")
                                }
                                else -> {
                                    setError("An unexpected error occurred")
                                }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            setError("Something went wrong, please try again")
        }
    }

    fun getAllLocationsFromDb() {
        val deviceId = _state.value.device?.id
        if (deviceId != null) {
            getAllLocationsUseCase(credentials = _state.value.token, deviceId = deviceId.toString())
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            if (result.data != null) {
                                setLocationHistory(result.data)
                            }
                        }
                        is Resource.Error -> {
                            when (result.code) {
                                401 -> {
                                    setError("Try to re-login")
                                }
                                404 -> {
                                    setError("This device doesn't exist, try to re-login")
                                }
                                409 -> {
                                    setError("Can't find the device's location")
                                }
                                -1 -> {
                                    setError("Internet connection error")
                                }
                                else -> {
                                    setError("An unexpected error occurred")
                                }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            setError("Something went wrong, please try again")
        }
    }

    fun getLocationFromDb() {
        val deviceId = _state.value.device?.id
        if (deviceId != null) {
            getLocationUseCase(credentials = _state.value.token, deviceId = deviceId.toString())
                .onEach { result ->
                    when (result) {
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            if (result.data != null) {
                                setLocationLatest(result.data)
                            }
                        }
                        is Resource.Error -> {
                            when (result.code) {
                                401 -> {
                                    setError("Try to re-login")
                                }
                                404 -> {
                                    setError("This device doesn't exist, try to re-login")
                                }
                                409 -> {
                                    setError("Can't find the device's location")
                                }
                                -1 -> {
                                    setError("Internet connection error")
                                }
                                else -> {
                                    setError("An unexpected error occurred")
                                }
                            }
                        }
                    }
                }.launchIn(viewModelScope)
        } else {
            setError("Something went wrong, please try again")
        }
    }

    suspend fun updateCameraPosition() {
        if (state.value.locationLatest != null) {
            state.value.cameraPositionState.animate(
                CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(LatLng(state.value.locationLatest!!.latitude, state.value.locationLatest!!.longitude), defaultZoom))
            )
        } else {
            return
        }
    }

    // TODO: prerobit na getPeriodicLocations()
//    private suspend fun getPeriodicLocations() {
//            while(true) {
//                getLocationFromDb()
//                delay(DELAY_REFRESH)
//            }
//    }

    private fun setDevice(newValue: Device) {
        _state.value = state.value.copy(
            device = newValue
        )
    }

    fun setLocationHistory(newValue: List<LocationDto>) {
        _state.value = state.value.copy(
            locationHistory = newValue
        )
    }

    private fun setLocationLatest(newValue: LocationDto) {
        _state.value = state.value.copy(
            locationLatest = newValue
        )
    }

    private fun removeGeofence() {
        _state.value = state.value.copy(
            deviceGeofenceVertices = emptyList()
        )
    }

    private fun setDeviceGeofenceVertices(vertices: List<GeofenceVertex>) {
        _state.value = state.value.copy(
            deviceGeofenceVertices = vertices
        )
    }

    private fun removeDevice(deviceId: Int) {
        val updatedDevices = state.value.devices.filter { it.id != deviceId }
        _state.value = state.value.copy(
            devices = updatedDevices
        )
    }

    private fun addDevice(newDevice: Device) {
        _state.value = state.value.copy(
            devices = state.value.devices + newDevice
        )
    }

    private fun setDevices(devices: List<Device>) {
        _state.value = state.value.copy(
            devices = devices
        )
    }


    private fun setError(message: String?) {
        _state.value = state.value.copy(
            error = message
        )
    }

    private fun setToken(newValue: String) {
        _state.value = state.value.copy(
            token = newValue
        )
    }
}