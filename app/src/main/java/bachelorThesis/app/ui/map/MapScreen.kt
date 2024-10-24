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
            viewModel.getLocationFromDb()
            viewModel.getAllLocationsFromDb()
//            viewModel.getGeofenceFromDb()
            viewModel.updateCameraPosition() // TODO: toto nefunguje
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
                        name = "-"
                    )
                }
            },
            floatingActionButton = {
                Row (horizontalArrangement = Arrangement.Center) {
                    FloatingActionButton(
                        onClick = {
                            localCoroutineScope.launch {
                                viewModel.setShowLocationHistory(!state.showLocationHistory)
                            }
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
                            localCoroutineScope.launch { 
                                viewModel.setShowGeofence(!state.showGeofence)
                            }
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
                            localCoroutineScope.launch { 
                                viewModel.updateCameraPosition() 
                            }
                        },
                        Modifier.background( color = colorScheme.primary)
                    ) {
                        Icon(
                            painter = painterResource(id = IconResource.Place.id ),
                            contentDescription = "center_icon",
                        )
                    }
                }
            },
            // TODO : s tymto nieco urobit
//            if (state.AddingGeofence) {
//                FloatingActionButton(
//                    onClick = { /* Implement geofence creation action */ },
//                    modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
//                    content = {
//                        Icon(Icons.Default.Add, contentDescription = "Create Geofence")
//                    }
//                )
//            }
        ) {
            MapScreenContent( // TODO: toto dorobit
                state,
                viewModel
            ) {}
        }
    }

}

@Composable
fun ModalDrawerContent(
    navigator: DestinationsNavigator,
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
                navigator = navigator,
                viewModel = viewModel,
                closeDrawer = closeDrawer,
                onNavigateToAddDevice = { onContentChange(DrawerContentType.ADD_NEW_DEVICE) },
                onNavigateToMyDevices = { onContentChange(DrawerContentType.MY_DEVICES) },
                onNavigateToGeofence = { onContentChange(DrawerContentType.GEOFENCE) }
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
                onBackToMenu = { onContentChange(DrawerContentType.MAIN_MENU) }
            )
        }
    }
}


@Composable
fun MainMenuContent(
    navigator: DestinationsNavigator,
    viewModel: MapScreenViewModel,
    closeDrawer: () -> Unit,
    onNavigateToAddDevice: () -> Unit,
    onNavigateToMyDevices: () -> Unit,
    onNavigateToGeofence: () -> Unit
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
        label = { Text(text = "Geofence") },
        selected = false,
        onClick = { onNavigateToGeofence() }
    )
    HorizontalDivider()
    NavigationDrawerItem(
        label = { Text(text = "Logout") },
        selected = false,
        onClick = { // TODO: WIP
            viewModel.setLogout()
            navigator.navigate(HomeScreenDestination)
        }
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
    onBackToMenu: () -> Unit,
    closeDrawer: () -> Unit,
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
                        closeDrawer()
                    }
                )
                HorizontalDivider()
            }
        }
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

    NavigationDrawerItem(
        label = { Text(text = "Back to Menu") },
        icon = { Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) },
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
                text = "Add New Device",
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

@Composable
fun GeofenceContent(
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

    if (state.device != null) {
        if (state.deviceGeofenceVertices.isEmpty()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add Geofence for ${state.device.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // TODO
                // Add geofence-specific content here (e.g., form fields to add geofence)
                // Example: Input fields, buttons, etc.
            }
        } else {
            SwipeableNavigationDrawerItem(
                text = "${state.device.name}'s Geofence",
                onDelete = { viewModel.removeGeofenceFromDb() },
                onItemClicked = { /* TODO */ }
            )
        }
    } else {
        Column {
            Text(
                text = "No device selected.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
    viewModel: MapScreenViewModel,
    updateCameraCallback: () -> Unit
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
        onMapLoaded = {
            updateCameraCallback()
        },
        onMapClick = { latLng -> // TODO: otestovat
            if (state.addingGeofence) {
                viewModel.onGeofencePointAdded(latLng)
            }
        }
    ) {
        if (state.locationLatest != null) { // TODO: pridat nejaku icon mozno
            Marker(state = rememberMarkerState(position = LatLng(state.locationLatest!!.latitude, state.locationLatest!!.longitude)))
        }
        if (state.locationHistory.isNotEmpty()) {
            if (state.showLocationHistory) {
                Polyline(points = state.locationHistory.map { LatLng(it.latitude, it.longitude) })
            }
        }
        if (state.deviceGeofenceVertices.isNotEmpty() && state.showGeofence) {
            Polygon(
                points = state.deviceGeofenceVertices.map { LatLng(it.latitude, it.longitude) },
                fillColor = Color.Transparent,
                strokeColor = Color(green = 178, red = 102, blue = 255),
                strokeJointType = JointType.BEVEL
            )
            // TODO: setError ak showGeofence true ale on neexistuje
        }
    }
}