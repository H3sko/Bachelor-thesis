package bachelorThesis.app.ui.registration

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import bachelorThesis.app.ui.destinations.HomeScreenDestination
import bachelorThesis.app.ui.destinations.LoginScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun RegistrationScreen(
    navigator: DestinationsNavigator,
    viewModel: RegistrationScreenViewModel = hiltViewModel()
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
            RegistrationScreenTopBar(
                popBackStackCallback = {
                    navigator.navigate(HomeScreenDestination())
                }
            )
        }
    ) {
        if (state.success) {
            SuccessfulRegistration(navigator)
        } else {
            RegistrationScreenContent(
                state,
                viewModel::register,
                viewModel::setUsername,
                viewModel::setPassword
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreenTopBar(
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
                    text = "Register"
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
fun RegistrationScreenContent(
    state: RegistrationScreenState,
    registerCallback: () -> Unit,
    setLoginCallback: (String) -> Unit,
    setPasswordCallback: (String) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxSize(),
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
                Text("Register")
            },
            onClick = {
                registerCallback()
            },
            modifier = Modifier.testTag("submit")
        )
    }
}

@Composable
fun SuccessfulRegistration(navigator: DestinationsNavigator) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Registration Successful!",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                navigator.navigate(LoginScreenDestination())
            }
        ) {
            Text(text = "Login")
        }
    }
}
