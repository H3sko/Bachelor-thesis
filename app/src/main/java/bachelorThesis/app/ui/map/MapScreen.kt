@file:OptIn(ExperimentalMaterial3Api::class)

package bachelorThesis.app.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bachelorThesis.app.common.IconResource
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun MapScreen(
    navigator: DestinationsNavigator,
    viewModel: MapScreenViewModel = hiltViewModel()
) {
    val localCoroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state

    LaunchedEffect(state) {
        if (state.error != null) {
            localCoroutineScope.launch { snackbarHostState.showSnackbar(
                message = state.error!!,
                duration = SnackbarDuration.Short
            )}
        }
        // TODO: ?
//        if (state.deviceId != null) {
//            getPeriodicLocations()
//        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            MapScreenTopBar(
                popNavBackStackCallback = { navigator.popBackStack() },
                name = state.deviceName,
            )
        },
        floatingActionButton = {
            Column {
                FloatingActionButton(
                    onClick = {
                        if(state.locationHistory.isNotEmpty()) viewModel.setLocationHistory(emptyList())
                        else viewModel.getAllLocationsFromDb()
                    },
                    Modifier.background( color =  if (state.locationHistory.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        painter = painterResource(id = IconResource.Route.id ),
                        contentDescription = "polyline_icon"
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                FloatingActionButton(
                    onClick = {
                        localCoroutineScope.launch { viewModel.updateCameraPosition() }
                    },
                    Modifier.background( color = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        painter = painterResource(id = IconResource.Place.id ),
                        contentDescription = "center_icon",
                    )
                }
            }
        }
    ) {
        MapScreenContent(
            state,
        ) {
            localCoroutineScope.launch { viewModel.updateCameraPosition() }
        }
    }
}

@Composable
private fun MapScreenTopBar(
    popNavBackStackCallback: () -> Unit,
    name: String,
) {
    TopAppBar(
        title = {
            Box(modifier = Modifier.fillMaxSize()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset((-35).dp)
                ) {
                    Text(
                        text = name,
                    )
                }
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "BackIcon",
                modifier = Modifier
                    .clickable(onClick = {
                        popNavBackStackCallback()
                    })
                    .padding(start = 10.dp)
            )
        }
    )
}

@Composable
private fun MapScreenContent(
    state: MapScreenState,
    updateCameraCallback: () -> Unit
) {

    val uiSettings = remember {
        MapUiSettings(
            zoomGesturesEnabled = true,
            zoomControlsEnabled = false
        )
    }

    GoogleMap (
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = state.cameraPositionState,
        uiSettings = uiSettings,
        onMapLoaded = {
            updateCameraCallback()
        }
    ) {
        if (state.locationLatest != null) {
            Marker(rememberMarkerState(position = LatLng(state.locationLatest.latitude, state.locationLatest.longitude)))
        }
        if (state.locationHistory.isNotEmpty()) {
            Polyline(points = state.locationHistory.map { LatLng(it.latitude, it.longitude) })
        }
    }
}