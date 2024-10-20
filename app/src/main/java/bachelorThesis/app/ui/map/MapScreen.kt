@file:OptIn(ExperimentalMaterial3Api::class)

package bachelorThesis.app.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bachelorThesis.app.common.DrawerContentType
import bachelorThesis.app.common.IconResource
import bachelorThesis.app.data.remote.dto.Device
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.CoroutineScope
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
    }
    LaunchedEffect(state.device) {
        state.device?.let { device ->
            viewModel.getGeofenceFromDb()
            viewModel.getLocationFromDb()
            viewModel.getAllLocationsFromDb()
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentContent by remember { mutableStateOf(DrawerContentType.MAIN_MENU) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerContent(
                state = state,
                viewModel = viewModel,
                modifier = Modifier,
                closeDrawer = { localCoroutineScope.launch { drawerState.close() } },
                currentContent = currentContent,
                onContentChange = { newContent -> currentContent = newContent }
            )
        }
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                state.device?.let {
                    MapScreenTopBar(
                        localCoroutineScope,
                        drawerState,
                        name = it.name
                    )
                }
            },
            floatingActionButton = {
                Row {
                    FloatingActionButton(
                        onClick = {
                            if(state.locationHistory.isNotEmpty()) viewModel.setLocationHistory(emptyList())
                            else viewModel.getAllLocationsFromDb()
                        },
                        Modifier.background( color =  if (state.locationHistory.isNotEmpty()) colorScheme.primary else colorScheme.secondary)
                    ) {
                        Icon(
                            painter = painterResource(id = IconResource.Route.id ),
                            contentDescription = "polyline_icon"
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            // TODO: showPolygon()
                            localCoroutineScope.launch { }
                        },
                        Modifier.background( color = colorScheme.primary)
                    ) {
                        Icon(
                            painter = painterResource(id = IconResource.Polygon.id ),
                            contentDescription = "center_icon",
                        )
                    }
                    FloatingActionButton(
                        onClick = {
                            localCoroutineScope.launch { viewModel.updateCameraPosition() }
                        },
                        Modifier.background( color = colorScheme.primary)
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
                state
            ) {
                localCoroutineScope.launch { viewModel.updateCameraPosition() }
            }
        }
    }

}

@Composable
fun ModalDrawerContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    modifier: Modifier,
    closeDrawer: () -> Unit,
    currentContent: DrawerContentType,
    onContentChange: (DrawerContentType) -> Unit
) {
    ModalDrawerSheet(modifier = modifier) {
        when (currentContent) {
            DrawerContentType.MAIN_MENU -> MainMenuContent(
                closeDrawer = closeDrawer,
                onNavigateToAddDevice = { onContentChange(DrawerContentType.ADD_NEW_DEVICE) },
                onNavigateToMyDevices = { onContentChange(DrawerContentType.MY_DEVICES) }
            )
            DrawerContentType.MY_DEVICES -> MyDevicesContent(
                state = state,
                viewModel = viewModel,
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) }
            )
            DrawerContentType.ADD_NEW_DEVICE -> AddNewDeviceContent(
                state = state,
                viewModel = viewModel,
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) }
            )
        }
    }
}


@Composable
fun MainMenuContent(
    closeDrawer: () -> Unit,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToMyDevices: () -> Unit
) {
    NavigationDrawerItem(
        onClick = { closeDrawer() },
        icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
        label = { Text(text = "Home") },
        selected = false
    )
    HorizontalDivider()
    NavigationDrawerItem(
        label = { Text(text = "Add new device") },
        selected = false,
        onClick = { onNavigateToAddDevice() }
    )
    HorizontalDivider()
    NavigationDrawerItem(
        label = { Text(text = "My devices") },
        selected = false,
        onClick = { onNavigateToMyDevices() }
    )
    HorizontalDivider()
    NavigationDrawerItem(
        label = { Text(text = "Logout") },
        selected = false,
        onClick = { closeDrawer() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableNavigationDrawerItem(
    text: String,
    onDelete: () -> Unit,
    onItemClicked: () -> Unit
) {
    val swipeState = rememberSwipeToDismissBoxState(
        initialValue = SwipeToDismissBoxValue.Settled,
        confirmValueChange = { newValue ->
            if (newValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = swipeState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        NavigationDrawerItem(
            label = { Text(text) },
            selected = false,
            onClick = onItemClicked,
            icon = { Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clipToBounds()
        )
    }
}

@Composable
fun MyDevicesContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    onBackToMenu: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(text = "Back to Menu") },
        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
        selected = false,
        onClick = { onBackToMenu() }
    )
    HorizontalDivider()

    if ( state.devices.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No Devices", style = MaterialTheme.typography.bodyLarge)
        }
    } else {
        LazyColumn {
            items(state.devices, key = { device -> device.id }) { device ->
                SwipeableNavigationDrawerItem(
                    text = device.name,
                    onDelete = { viewModel.removeDeviceFromDb(device.id) },
                    onItemClicked = {
                        viewModel.setDevice(device)

                        viewModel.updateCameraPosition()
                        onBackToMenu()
                    } // TODO: ked sa zmeni device, mala by sa okamzite aktualizovat jeho aktualna poloha a stiahnut historia poloh + geofence
                )
                HorizontalDivider()
            }
        }
    }
}

// TODO: treba otestovat uz so zapnutou databazou
@Composable
fun AddNewDeviceContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    onBackToMenu: () -> Unit
) {
    var deviceName by remember { mutableStateOf("") }
    var deviceOwner by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    NavigationDrawerItem(
        label = { Text(text = "Back to Menu") },
        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
        selected = false,
        onClick = { onBackToMenu() }
    )
    HorizontalDivider()

    Text(text = "Add New Device", style = MaterialTheme.typography.bodyLarge)

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = deviceName,
            onValueChange = { deviceName = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = deviceOwner,
            onValueChange = { deviceOwner = it },
            label = { Text("Owner") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.addDeviceToDb(deviceName, deviceOwner)
                if (state.message != null) {
                    if (state.message == "Airtag added successfully"){
                        successMessage = "Airtag added successfully!"
                        deviceName = ""
                        deviceOwner = ""
                    } else {
                        successMessage = state.message
                    }
                } else {
                    successMessage = "Something went wrong. Please try again."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Submit")
        }

        if (successMessage.isNotEmpty()) {
            Text(
                text = successMessage,
                color = if (state.message == "Airtag added successfully") Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}




@Composable
private fun MapScreenTopBar(
    localCoroutineScope: CoroutineScope,
    drawerState: DrawerState,
    name: String
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
            IconButton(onClick = {
                localCoroutineScope.launch { drawerState.open() }
            }, content = {
                Icon(
                    imageVector = Icons.Default.Menu, contentDescription = null
                )
            }
            )
        })
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