package cz.mendelu.pef.xchomo.filmbuddy.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cz.mendelu.pef.xchomo.filmbuddy.R
import cz.mendelu.pef.xchomo.filmbuddy.navigation.INavigationRouter
import cz.mendelu.pef.xchomo.filmbuddy.ui.theme.elements.BackArrowScreen
import kotlinx.coroutines.delay
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigation: INavigationRouter,
    viewModel: LoginViewModel = getViewModel(),
)
{

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                }
            )
        }
    ) {
        LoginScreenContent(paddingValues = it, navigation = navigation, viewModel = viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    paddingValues: PaddingValues,
    navigation:INavigationRouter,
    viewModel: LoginViewModel
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val loginStatus by viewModel.loginStatus.observeAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(email = email, password = password)

                },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.login))
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (loginStatus) {
            LoginStatus.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }
            LoginStatus.Success -> {
                Toast.makeText(LocalContext.current, stringResource(R.string.login_success), Toast.LENGTH_SHORT).show()
                LaunchedEffect(Unit) {
                    delay(1000) // Add a short delay before navigation
                    navigation.navigateToHomeScreen()
                }
            }
            is LoginStatus.Error -> {
                val errorMessage = (loginStatus as LoginStatus.Error).errorMessage
                Text(errorMessage, color = Color.Red)
            }
            else -> { /* Do nothing */ }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = { navigation.navigateToRegisterScreen() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.dont))
        }
    }
}