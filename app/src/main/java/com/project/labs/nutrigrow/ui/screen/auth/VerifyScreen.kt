package com.project.labs.nutrigrow.ui.screen.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.data.model.VerifyModel
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.navigation.Screen
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    navController: NavHostController = rememberNavController(),
    redirectToRegister: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
) {
    var otpCode by remember { mutableStateOf("") }

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val otp: UiState<VerifyModel> by viewModel.otpCode
    var register by remember { mutableStateOf("Verifikasi") }

    DisposableEffect(key1 = otp){
        when (otp) {
            is UiState.Loading -> {
                register = "Loading..."
                coroutineScope.launch {
                    kotlinx.coroutines.delay(500)
                    register = "Verifikasi"
                }
            }
            is UiState.Error -> {
                register = "Verifikasi"
                coroutineScope.launch {
                    snackState.showSnackbar((otp as UiState.Error).errorMessage)
                    viewModel.resetOtpState()
                }
            }
            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Verifikasi Akun Berhasil\nSilahkan Lengkapi Data Anda")
                    redirectToRegister()
                    viewModel.resetOtpState()
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
                    containerColor = Color(0xFFE0FFD2),
                    navigationIconContentColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                title = {
                    Text(
                        text = "Registrasi",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
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
                .background(Color(0xFFE0FFD2))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState())
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 25.dp, end = 25.dp, top = 5.dp, bottom = 15.dp),
                ) {
                    ElevatedCard(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors(Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        Text(
                            text = "Verifikasi Akun",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 10.dp,
                                    end = 10.dp,
                                    top = 8.dp,
                                    bottom = 8.dp
                                )
                        )
                    }
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        color = Color(0xFF00BF63),
                        thickness = 3.dp
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Masukan Kode OTP yang telah dikirimkan ke email anda",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Kode OTP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = otpCode,
                            onValueChange = { otpCode = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = { Icon(painterResource(id = R.drawable.baseline_verified_user_24), contentDescription = "Email", modifier = Modifier.size(22.dp)) },
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("_ _ _ _", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (register != "Loading...") viewModel.verifyOtp(
                                otpCode = otpCode
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    ) {
                        Text(
                            register,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Email yang anda masukan salah? ",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "Daftar",
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            style = MaterialTheme
                                .typography.titleMedium
                                .copy(textDecoration = TextDecoration.Underline),
                            modifier = Modifier
                                .clickable {
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
                    drawableRes = if (snackbarData.visuals.message.startsWith("Verifikasi Akun Berhasil")) R.drawable.baseline_check_circle_outline_24 else R.drawable.baseline_error_outline_24,
                    message = snackbarData.visuals.message,
                    containerColor = if (snackbarData.visuals.message.startsWith("Verifikasi Akun Berhasil")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}