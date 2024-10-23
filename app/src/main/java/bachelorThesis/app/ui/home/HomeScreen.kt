@file:OptIn(ExperimentalMaterial3Api::class)

package bachelorThesis.app.ui.home

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bachelorThesis.app.ui.destinations.DirectionDestination
import bachelorThesis.app.ui.destinations.LoginScreenDestination
import bachelorThesis.app.ui.destinations.RegistrationScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            HomeScreenTopBar(
                popBackStackCallback = {
                    navigator.popBackStack()
                }
            )
        }
    ) {
        HomeScreenContent(
            navigator = navigator
        )
    }



}

@Composable
fun HomeScreenContent(
    navigator: DestinationsNavigator
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Title(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.75f))
        DestinationButtons(navigator = navigator, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.25f))
    }
}
@Composable
fun Title(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "AirTag tracker",
            color = Color.Black,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

@Composable
fun DestinationButtons(
    navigator: DestinationsNavigator,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            Modifier.fillMaxWidth()
        ){
            HomeScreenButton(
                value = "Login",
                destination = LoginScreenDestination,
//                destination = MapScreenDestination, // TODO: ked chcem testovat mapu bez loginu
                navigator = navigator,
                modifier = Modifier
                    .fillMaxWidth(fraction = 0.5f)
                    .fillMaxHeight()
            )
            HomeScreenButton(
                value = "Register",
                destination = RegistrationScreenDestination,
                navigator = navigator,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun HomeScreenButton(
    value: String,
    destination: DirectionDestination,
    navigator: DestinationsNavigator,
    modifier: Modifier
) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Button(
            onClick = {
                navigator.navigate(destination)
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 5.dp, vertical = 1.dp)
        ) {
            Text(text = value)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenTopBar(
    popBackStackCallback: () -> Unit
) {
    TopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .offset((-35).dp)
            ) {
                Text(
                    text = ""
                )
            }
        },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "DrawerIcon",
                modifier = Modifier
                    .clickable(onClick = {
                        popBackStackCallback()
                    })
                    .padding(start = 10.dp)
            )
        }
    )
}
