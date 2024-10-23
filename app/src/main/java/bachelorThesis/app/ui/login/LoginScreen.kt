package bachelorThesis.app.ui.login

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bachelorThesis.app.ui.destinations.HomeScreenDestination
import bachelorThesis.app.ui.destinations.MapScreenDestination
import bachelorThesis.app.ui.destinations.RegistrationScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator,
    viewModel: LoginScreenViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.state

    LaunchedEffect(state) {
        if (state.error != null) {
            coroutineScope.launch { snackbarHostState.showSnackbar(
                message = state.error!!,
                duration = SnackbarDuration.Short
            )}
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            LoginScreenTopBar(
                popBackStackCallback = {
                    navigator.navigate(HomeScreenDestination())
                }
            )
        }
    ) {
        if (state.success) {
            navigator.navigate(MapScreenDestination())
            // TODO: len na testovanie loginu
//            SuccessfulLogin(
//                navigator,
//                viewModel::logOut
//            )
        } else {
            LoginScreenContent(
                state,
                { runBlocking {  viewModel.logIn() } },
                { newValue: String -> viewModel.setUsername(newValue) },
                { newValue: String -> viewModel.setPassword(newValue) },
                navigator = navigator
            )
        }
    }
}

@Composable
fun SuccessfulLogin( // TODO: toto som pouzival asi len na testovanie takze mozno vymazat
    navigator: DestinationsNavigator,
    logOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Yay, successfully logged in!",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                logOut()
                navigator.navigate(HomeScreenDestination())
            }
        ) {
            Text(text = "Log Out")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun LoginScreenTopBar(
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
                    text = "Login"
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

@Composable
fun LoginScreenContent(
    state: LoginScreenState,
    loginCallback: () -> Unit,
    setLoginCallback: (String) -> Unit,
    setPasswordCallback: (String) -> Unit,
    navigator: DestinationsNavigator
) {
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        LoginForm(state, loginCallback, setLoginCallback, setPasswordCallback, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.8f))
        NavigateToRegistrationButton(navigator = navigator, modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.2f))
    }
}

@Composable
fun LoginForm(
    state: LoginScreenState,
    loginCallback: () -> Unit,
    setLoginCallback: (String) -> Unit,
    setPasswordCallback: (String) -> Unit,
    modifier: Modifier
) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = state.username,
            onValueChange = {
                    changedValue -> setLoginCallback(changedValue)
            },
            label = {
                Text("Username")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Login icon"
                )
            },
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.testTag("login")
        )
        Spacer(Modifier.height(20.dp))

        TextField(
            value = state.password,
            onValueChange = {
                    changedValue -> setPasswordCallback(changedValue)
            },
            label = {
                Text("Password")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "PasswordIcon"
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                cursorColor = MaterialTheme.colorScheme.onPrimary,
                disabledIndicatorColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier.testTag("password")
        )

        Spacer(Modifier.height(20.dp))

        Button(
            content = {
                Text("Login")
            },
            onClick = {
                loginCallback()
            },
            modifier = Modifier.testTag("submit")
        )
    }
}

@Composable
fun NavigateToRegistrationButton(
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
                navigator.navigate(RegistrationScreenDestination())
            }
        ) {
            Text(text = "Register")
        }
    }
}
