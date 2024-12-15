@file:OptIn(ExperimentalMaterial3Api::class)

package bachelorThesis.app.ui.map

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerDefaults
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
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import bachelorThesis.app.common.DrawerContentType
import bachelorThesis.app.common.IconResource
import bachelorThesis.app.ui.destinations.HomeScreenDestination
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
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

    LaunchedEffect(state.error) {
        if (state.error != null) {
            localCoroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = state.error!!,
                    duration = SnackbarDuration.Short
                )
                viewModel.setError(null)
            }
        }
    }
    LaunchedEffect(state.device) {
        state.device?.let {
            viewModel.getLocationFromDb()
            viewModel.getAllLocationsFromDb()
            viewModel.getGeofenceFromDb()
            localCoroutineScope.launch { viewModel.updateCameraPosition() } // TODO: toto nefunguje
        } ?: run {
            viewModel.setDeviceGeofenceVertices(emptyList())
            viewModel.setLocationLatest(null)
            viewModel.setLocationHistory(emptyList())
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentContent by remember { mutableStateOf(DrawerContentType.MAIN_MENU) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerContent(
                navigator = navigator,
                state = state,
                localCoroutineScope = localCoroutineScope,
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
                } ?: run {
                    MapScreenTopBar(
                        localCoroutineScope,
                        drawerState,
                        name = ""
                    )
                }
            },
            floatingActionButton = {
                if (!state.addingGeofence) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                localCoroutineScope.launch {
                                    viewModel.setShowLocationHistory(!state.showLocationHistory)
                                }
                            },
                            containerColor = colorScheme.primary
                        ) {
                            Icon(
                                painter = painterResource(id = IconResource.Route.id),
                                contentDescription = "polyline_icon"
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                localCoroutineScope.launch {
                                    viewModel.setShowGeofence(!state.showGeofence)
                                }
                            },
                            containerColor = colorScheme.primary
                        ) {
                            Icon(
                                painter = painterResource(id = IconResource.Polygon.id),
                                contentDescription = "geofence_icon"
                            )
                        }

                        FloatingActionButton(
                            onClick = {
                                localCoroutineScope.launch {
                                    viewModel.updateCameraPosition()
                                }
                            },
                            containerColor = colorScheme.primary
                        ) {
                            Icon(
                                painter = painterResource(id = IconResource.Place.id),
                                contentDescription = "center_icon"
                            )
                        }
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        FloatingActionButton(
                            onClick = {
                                if (state.addedGeofenceVertices.size < 3) {
                                    viewModel.setError("Select at least 3 points")
                                } else {
                                    viewModel.addGeofenceToDb()
                                }
                            },
                            containerColor = Color.Green,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Create", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        FloatingActionButton(
                            onClick = {
                                viewModel.setAddedGeofenceVertices(emptyList())
                                viewModel.setAddingGeofence(false)
                            },
                            containerColor = Color.Red,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

        ) {
            MapScreenContent(
                state = state,
                viewModel = viewModel
            )
        }
    }

}

@Composable
fun ModalDrawerContent(
    navigator: DestinationsNavigator,
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    localCoroutineScope: CoroutineScope,
    modifier: Modifier,
    closeDrawer: () -> Unit,
    currentContent: DrawerContentType,
    onContentChange: (DrawerContentType) -> Unit
) {
    ModalDrawerSheet(modifier = modifier) {
        when (currentContent) {
            DrawerContentType.MAIN_MENU -> MainMenuContent(
                navigator = navigator,
                viewModel = viewModel,
                localCoroutineScope = localCoroutineScope,
                closeDrawer = closeDrawer,
                onNavigateToAddDevice = { onContentChange(DrawerContentType.ADD_NEW_DEVICE) },
                onNavigateToMyDevices = { onContentChange(DrawerContentType.MY_DEVICES) },
                onNavigateToGeofence = { onContentChange(DrawerContentType.GEOFENCE) },
                onNavigateToNotifications = { onContentChange(DrawerContentType.NOTIFICATIONS) }
            )
            DrawerContentType.MY_DEVICES -> MyDevicesContent(
                state = state,
                viewModel = viewModel,
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) },
                closeDrawer = closeDrawer
            )
            DrawerContentType.ADD_NEW_DEVICE -> AddNewDeviceContent(
                state = state,
                viewModel = viewModel,
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) }
            )
            DrawerContentType.GEOFENCE -> GeofenceContent(
                state = state,
                viewModel = viewModel,
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) },
                closeDrawer = closeDrawer
            )
            DrawerContentType.NOTIFICATIONS -> NotificationsContent(
                state = state,
                viewModel = viewModel,
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) }
            )
        }
    }
}


@Composable
fun MainMenuContent(
    navigator: DestinationsNavigator,
    viewModel: MapScreenViewModel,
    localCoroutineScope: CoroutineScope,
    closeDrawer: () -> Unit,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToMyDevices: () -> Unit,
    onNavigateToGeofence: () -> Unit,
    onNavigateToNotifications: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            NavigationDrawerItem(
                onClick = { closeDrawer() },
                icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                label = {  },
                selected = false
            )
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text(text = "Add new AirTag") },
                selected = false,
                onClick = { onNavigateToAddDevice() }
            )
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text(text = "My AirTags") },
                selected = false,
                onClick = { onNavigateToMyDevices() }
            )
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text(text = "Geofence") },
                selected = false,
                onClick = { onNavigateToGeofence() }
            )
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text(text = "Notifications") },
                selected = false,
                onClick = { onNavigateToNotifications() }
            )
            HorizontalDivider()
        }

        Button(
            onClick = {
                localCoroutineScope.launch { viewModel.setLogout() }
                navigator.navigate(HomeScreenDestination)
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text(text = "Logout")
        }
    }
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
            icon = {
                Image(
                    painter = painterResource(id = IconResource.Airtag.id),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(DrawerDefaults.modalContainerColor)
                .clipToBounds()
        )
    }
}

@Composable
fun AddNewDeviceContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    onBackToMenu: () -> Unit
) {
    var deviceName by remember { mutableStateOf("") }
    var deviceOwner by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    LaunchedEffect(state.message) {
        state.message?.let { message ->
            if (message == "Airtag added successfully") {
                successMessage = "Airtag added successfully!"
                deviceName = ""
                deviceOwner = ""
            } else {
                successMessage = message
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavigationDrawerItem(
            label = { },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = {
                onBackToMenu()
                viewModel.setMessage(null)
            }
        )
        HorizontalDivider()

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Add New AirTag",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit")
            }

            if (successMessage.isNotEmpty()) {
                Text(
                    text = successMessage,
                    color = if (state.message == "Airtag added successfully!") Color.Green else Color.Red,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
fun MyDevicesContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    onBackToMenu: () -> Unit,
    closeDrawer: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavigationDrawerItem(
            label = { },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = {
                onBackToMenu()
                viewModel.setMessage(null)
            }
        )
        HorizontalDivider()

        if (state.devices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No AirTags", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn {
                items(state.devices, key = { device -> device.id }) { device ->
                    SwipeableNavigationDrawerItem(
                        text = device.name,
                        onDelete = { viewModel.removeDeviceFromDb(device.id) },
                        onItemClicked = {
                            closeDrawer()
                            viewModel.setDevice(device)
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun GeofenceContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    onBackToMenu: () -> Unit,
    closeDrawer: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavigationDrawerItem(
            label = {  },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = { onBackToMenu() }
        )
        HorizontalDivider()

        if (state.device != null) {
            if (state.deviceGeofenceVertices.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            viewModel.setAddingGeofence(true)
                            closeDrawer()
                        },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Create Geofence")
                    }
                }
            } else {
                SwipeableNavigationDrawerItem(
                    text = "${state.device.name}'s Geofence",
                    onDelete = { viewModel.removeGeofenceFromDb() },
                    onItemClicked = {
                        viewModel.setShowGeofence(!state.showGeofence)
                        closeDrawer()
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No AirTag selected",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun NotificationsContent(
    state: MapScreenState,
    viewModel: MapScreenViewModel,
    onBackToMenu: () -> Unit
) {
    val isNotificationsEnabled = state.geofenceNotificationStatus

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NavigationDrawerItem(
            label = {  },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            },
            selected = false,
            onClick = { onBackToMenu() }
        )
        HorizontalDivider()

        Column {
            DrawerItemWithSwitch(
                label = "Enable Geofence Alerts",
                description = "Get notified when your AirTag leaves the designated area.",
                isChecked = isNotificationsEnabled,
                onCheckedChange = { viewModel.toggleGeofenceNotification() }
            )
        }
    }
}

@Composable
fun DrawerItemWithSwitch(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val isExpanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
        )

        IconButton(onClick = { isExpanded.value = !isExpanded.value }) {
            Icon(Icons.Default.Info, contentDescription = "Info")
        }

        if (isExpanded.value) {
            Text(text = "A geofence is a virtual boundary. When your AirTag leaves this area, you will receive an alert.")
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
    viewModel: MapScreenViewModel
) {

    val uiSettings = remember {
        MapUiSettings(
            zoomGesturesEnabled = true,
            zoomControlsEnabled = true
        )
    }

    GoogleMap (
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = state.cameraPositionState,
        uiSettings = uiSettings,
        onMapClick = { latLng ->
            if (state.addingGeofence) {
                viewModel.addGeofencePoint(latLng)
            }
        }
    ) {
        if (state.locationLatest != null) { // TODO: pridat nejaku icon mozno, upravit marker nech je krajsi
            Marker(state = rememberMarkerState(position = LatLng(state.locationLatest!!.latitude, state.locationLatest!!.longitude)))
        }
        if (state.locationHistory.isNotEmpty()) {
            if (state.showLocationHistory) {
                Polyline(points = state.locationHistory.map { LatLng(it.latitude, it.longitude) })
            }
        }
        if (state.showGeofence) {
            if (state.deviceGeofenceVertices.isNotEmpty()) {
                Polygon(
                    points = state.deviceGeofenceVertices.map { LatLng(it.latitude, it.longitude) },
                    fillColor = Color.Transparent,
                    strokeColor = Color.Red,
                    strokeJointType = JointType.BEVEL
                )
            } else {
                viewModel.setError("This AirTag does not have a geofence")
                viewModel.setShowGeofence(false)
            }
        }
        if (state.addedGeofenceVertices.isNotEmpty()) {
            if (state.addingGeofence) {
                for (point in state.addedGeofenceVertices) {
                    Marker(
                        state = rememberMarkerState(position = LatLng(point.latitude, point.longitude))
                    )
                }
            }
        }
    }
}