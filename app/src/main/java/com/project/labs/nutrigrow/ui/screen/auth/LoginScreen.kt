package com.project.labs.nutrigrow.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.navigation.Screen
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    redirectToHome: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
) {
    val auth: UiState<AuthModel> by viewModel.auth

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var logIn by remember { mutableStateOf("Masuk") }

    var isPasswordVisible by remember { mutableStateOf(false) }

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()


    DisposableEffect(key1 = auth ){
        when (auth) {
            is UiState.Loading -> {
                logIn = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    logIn = "Masuk"
                }
            }
            is UiState.Error -> {
                logIn = "Masuk"
                coroutineScope.launch {
                    snackState.showSnackbar((auth as UiState.Error).errorMessage)
                    viewModel.resetLoginState()
                }
            }
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Masuk\nWelcome to NutriGrow!")
                    redirectToHome()
                }
            }
            else -> {}
        }
        onDispose {  }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    navigationIconContentColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                title = {
                    Text(
                        text = "Masuk",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        },
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
                    .padding(vertical = 20.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.nutrigrow_logo_nobg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Selamat Datang Kembali!",
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp)
                )
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, top = 25.dp, bottom = 15.dp),
                ) {
                    Text(
                        text = "Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_person_24), contentDescription = "Email", modifier = Modifier.size(22.dp)) },
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("Nutrigrow@gmail.com", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)) },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 1,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Kata Sandi",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box (modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("••••••••", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.7f)) },
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (logIn != "Loading...") viewModel.login(email, password)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    ) {
                        Text(
                            text = logIn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Belum punya akun?",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "Registrasi",
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            style = MaterialTheme
                                .typography.titleMedium
                                .copy(textDecoration = TextDecoration.Underline),
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.CreateAccount.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
            SnackbarHost(
                modifier = Modifier.align(Alignment.TopCenter),
                hostState = snackState
            ) { snackbarData: SnackbarData ->
                CustomSnackBar(
                    drawableRes = if (snackbarData.visuals.message.startsWith("Berhasil")) R.drawable.baseline_check_circle_outline_24 else R.drawable.baseline_error_outline_24,
                    message = snackbarData.visuals.message,
                    containerColor = if (snackbarData.visuals.message.startsWith("Berhasil")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}