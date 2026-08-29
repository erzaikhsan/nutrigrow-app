package com.project.labs.nutrigrow.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavHostController = rememberNavController(),
    redirectToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val snackState = remember { SnackbarHostState() }

    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isCodeSent by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val forgotState by viewModel.forgot
    val resetState by viewModel.reset

    LaunchedEffect(forgotState) {
        when (val state = forgotState) {
            is UiState.Success -> {
                isCodeSent = true
                snackState.showSnackbar("Kode Pemulihan Terkirim ke Surel Anda")
                viewModel.resetForgotState()
            }
            is UiState.Error -> {
                snackState.showSnackbar(state.errorMessage)
                viewModel.resetForgotState()
            }
            else -> { }
        }
    }

    LaunchedEffect(resetState) {
        when (val state = resetState) {
            is UiState.Success -> {
                snackState.showSnackbar("Kata Sandi Berhasil Diperbarui")
                viewModel.resetResetState()
                redirectToLogin()
            }
            is UiState.Error -> {
                snackState.showSnackbar(state.errorMessage)
                viewModel.resetResetState()
            }
            else -> { }
        }
    }

    Scaffold(
        containerColor = Color(0xFFE0FFD2),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF00BF63))
                    .padding(vertical = 12.dp)
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White,
                    )
                }
                Text(
                    text = "Lupa Kata Sandi",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        },
    ) { paddingValue ->
        Box(
            modifier = modifier
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
                        .padding(start = 25.dp, end = 25.dp, top = 15.dp, bottom = 15.dp),
                ) {
                    ElevatedCard(
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        ),
                        colors = CardDefaults.cardColors(Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isCodeSent) "Masukkan Kode Pemulihan" else "Pulihkan Akun Anda",
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFF00BF63),
                        thickness = 3.dp
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = if (isCodeSent) {
                            "Kami telah mengirim kode pemulihan ke $email. Masukkan kode tersebut beserta kata sandi baru Anda."
                        } else {
                            "Masukkan surel yang terdaftar. Kami akan mengirimkan kode pemulihan ke surel tersebut."
                        },
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))
                    Text(
                        text = "Email",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W500,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        enabled = !isCodeSent,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_email_24),
                                contentDescription = "Surel",
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("Nutrigrow@gmail.com", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color(0xFF9DA1A6),
                            unfocusedIndicatorColor = Color(0xFF9DA1A6),
                            disabledIndicatorColor = Color(0xFF9DA1A6),
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isCodeSent) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Kode Pemulihan",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp)
                        )
                        OutlinedTextField(
                            value = otp,
                            onValueChange = { otp = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            shape = RoundedCornerShape(10.dp),
                            placeholder = { Text("123456", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Kata Sandi Baru",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp)
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            trailingIcon = {
                                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                    Icon(
                                        painter = painterResource(id = if (isPasswordVisible) R.drawable.baseline_visibility_24 else R.drawable.baseline_visibility_off_24),
                                        contentDescription = "Lihat Kata Sandi",
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Konfirmasi Kata Sandi Baru",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 5.dp)
                        )
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color(0xFF9DA1A6),
                                unfocusedIndicatorColor = Color(0xFF9DA1A6),
                                disabledIndicatorColor = Color(0xFF9DA1A6),
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (isCodeSent) {
                                viewModel.resetPassword(
                                    email = email,
                                    otp = otp,
                                    password = password,
                                    confirmation = confirmPassword,
                                )
                            } else {
                                viewModel.forgotPassword(email = email)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color(0xFF00BF63)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isCodeSent) "Perbarui Kata Sandi" else "Kirim Kode Pemulihan",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    if (isCodeSent) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum menerima kode? Kirim ulang",
                            fontSize = 14.sp,
                            color = Color(0xFF00BF63),
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }

            SnackbarHost(
                modifier = Modifier.align(Alignment.TopCenter),
                hostState = snackState
            ) { snackbarData: SnackbarData ->
                val berhasil = snackbarData.visuals.message.contains("Berhasil") ||
                    snackbarData.visuals.message.contains("Terkirim")
                CustomSnackBar(
                    drawableRes = if (berhasil) R.drawable.baseline_check_circle_outline_24 else R.drawable.baseline_error_outline_24,
                    message = snackbarData.visuals.message,
                    containerColor = if (berhasil) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
