package com.project.labs.nutrigrow.ui.screen.profile

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.profile.UpdateProfileActivity
import com.project.labs.nutrigrow.activity.report.ReportActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.utils.ReminderWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun ProfileScreen(
    redirectToWelcome: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
) {
    val context = LocalContext.current
    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showLogoutDialog by remember { mutableStateOf(false) }

    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            when (checkAuth) {
                is UiState.Success -> {
                    viewModel.getUserProfile((checkAuth as UiState.Success<AuthModel>).data.role)
                }
                else -> {}
            }
        }
    }

    fun testReminderNow() {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0FFD2))
    ){
        viewModel.user.collectAsState(initial = UiState.Loading).value.let { user ->
            when (user) {
                is UiState.Loading -> {
                    LoadingIndicator(modifier = Modifier)
                    when (checkAuth) {
                        is UiState.Success -> {
                            viewModel.getUserProfile((checkAuth as UiState.Success<AuthModel>).data.role)
                        }
                        else -> {}
                    }
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxSize()
                    ) {
                        item {
                            user.data.apply {
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 4.dp
                                    ),

                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Row (
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White)
                                            .padding(20.dp)
                                    ){
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            AsyncImage(
                                                model = if (user.data.gender == "M") R.drawable.man else R.drawable.woman,
                                                contentDescription = "Profile Image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .padding(4.dp)
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            when(checkAuth) {
                                                is UiState.Success -> {
                                                    ElevatedCard(
                                                        elevation = CardDefaults.cardElevation(
                                                            defaultElevation = 10.dp
                                                        ),
                                                        colors = if ((checkAuth as UiState.Success<AuthModel>).data.role == "Officer") {
                                                            CardDefaults.cardColors(Color(0xFF2C98EE))
                                                        } else {
                                                            CardDefaults.cardColors(Color(0xFF00BF63))
                                                        },
                                                        modifier = Modifier
                                                            .clip(shape = CardDefaults.shape)
                                                            .shadow(
                                                                elevation = 0.dp,
                                                                spotColor = Color.Transparent,
                                                                shape = CardDefaults.shape
                                                            )
                                                    ){
                                                        Text(
                                                            text = when((checkAuth as UiState.Success<AuthModel>).data.role) {
                                                                "Officer" -> "Petugas"
                                                                "Parent" -> "Orang Tua"
                                                                "Admin" -> "Admin"
                                                                else -> "Pengguna"
                                                            },
                                                            fontSize = 12.sp,
                                                            color = Color.White,
                                                            modifier = Modifier.padding( horizontal = 8.dp)
                                                        )
                                                    }
                                                }
                                                else -> {  }
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(20.dp))
                                        Column {
                                            Text(
                                                text =
                                                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin"){
                                                    user.data.full_name
                                                } else {
                                                    "${user.data.full_name} | ${
                                                        when (user.data.region) {
                                                            "RW1" -> "PAMUJI 1"
                                                            "RW2" -> "PAMUJI 2"
                                                            "RW3" -> "PAMUJI 3"
                                                            "RW4" -> "PAMUJI 4"
                                                            "RW5" -> "PAMUJI 5"
                                                            else -> "PAMUJI"
                                                        }
                                                    }"
                                                },
                                                style = MaterialTheme.typography.titleMedium,
                                            )
                                            Text(
                                                text = user.data.address,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            user.data.apply {
                                Spacer(modifier = Modifier.height(20.dp))
                                ElevatedCard(
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 6.dp
                                    ),

                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    Column {
                                        when (checkAuth) {
                                            is UiState.Success -> {
                                                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Parent" || (checkAuth as UiState.Success<AuthModel>).data.role == "Officer") {
                                                    Row (
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.White)
                                                            .padding(20.dp)
                                                            .clickable {
                                                                when (checkAuth) {
                                                                    is UiState.Success -> {
                                                                        activityLauncher.launch(
                                                                            Intent(
                                                                                context,
                                                                                UpdateProfileActivity::class.java
                                                                            ).apply {
                                                                                putExtra("role", (checkAuth as UiState.Success<AuthModel>).data.role)
                                                                            }
                                                                        )
                                                                    }
                                                                    else -> {}
                                                                }
                                                            }
                                                    ){
                                                        Icon(
                                                            imageVector = Icons.Default.Person,
                                                            contentDescription = "My Account",
                                                        )
                                                        Spacer(modifier = Modifier.width(20.dp))
                                                        Column {
                                                            Text(
                                                                text = "Akun saya",
                                                                style = MaterialTheme.typography.titleMedium,
                                                            )
                                                            Text(
                                                                text = "Lakukan perubahan pada akun Anda",
                                                                style = MaterialTheme.typography.bodySmall,
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowRight,
                                                            contentDescription = "",
                                                        )
                                                    }
                                                    Divider(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.White)
                                                            .padding(horizontal = 15.dp),
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                        thickness = 1.dp
                                                    )

                                                    Row (
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.White)
                                                            .padding(20.dp)
                                                            .clickable {
                                                                testReminderNow()
                                                                coroutineScope.launch {
                                                                    snackState.showSnackbar("Notifikasi dijadwalkan dalam 10 detik")
                                                                }
                                                            }
                                                    ){
                                                        Icon(
                                                            imageVector = Icons.Default.Notifications,
                                                            contentDescription = "Notification",
                                                        )
                                                        Spacer(modifier = Modifier.width(20.dp))
                                                        Column {
                                                            Text(
                                                                text = "Push Notifikasi",
                                                                style = MaterialTheme.typography.titleMedium,
                                                            )
                                                            Text(
                                                                text = "Aktifkan push notifikasi untuk mendapatkan informasi terbaru",
                                                                style = MaterialTheme.typography.bodySmall,
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowRight,
                                                            contentDescription = "",
                                                        )
                                                    }
                                                    Divider(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.White)
                                                            .padding(horizontal = 15.dp),
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                        thickness = 1.dp
                                                    )
                                                }
                                                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin") {
                                                    Row (
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.White)
                                                            .padding(20.dp)
                                                            .clickable {
                                                                activityLauncher.launch(
                                                                    Intent(
                                                                        context,
                                                                        ReportActivity::class.java
                                                                    )
                                                                )
                                                            }
                                                    ){
                                                        Icon(
                                                            imageVector = Icons.Default.Info,
                                                            contentDescription = "Report",
                                                        )
                                                        Spacer(modifier = Modifier.width(20.dp))
                                                        Column {
                                                            Text(
                                                                text = "Laporan Posyandu",
                                                                style = MaterialTheme.typography.titleMedium,
                                                            )
                                                            Text(
                                                                text = "Lihat laporan posyandu balita",
                                                                style = MaterialTheme.typography.bodySmall,
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.weight(1f))
                                                        Icon(
                                                            imageVector = Icons.Default.KeyboardArrowRight,
                                                            contentDescription = "",
                                                        )
                                                    }
                                                    Divider(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .background(Color.White)
                                                            .padding(horizontal = 15.dp),
                                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                                        thickness = 1.dp
                                                    )
                                                }
                                            }
                                            else -> {}
                                        }

                                        Row (
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White)
                                                .padding(20.dp)
                                                .clickable {
                                                    showLogoutDialog = true
                                                }
                                        ){
                                            Icon(
                                                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_logout),
                                                contentDescription = "Log Out",
                                                tint = Color.Red
                                            )
                                            Spacer(modifier = Modifier.width(20.dp))
                                            Column {
                                                Text(
                                                    text = "Keluar",
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                                Text(
                                                    text = "Amankan akun Anda lebih lanjut demi keamanan",
                                                    style = MaterialTheme.typography.bodySmall,
                                                )
                                            }
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = Icons.Default.KeyboardArrowRight,
                                                contentDescription = "",
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(message = user.errorMessage, modifier = Modifier)
                }

                is UiState.Unauthorized -> {
                    redirectToWelcome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                }

                else -> {}
            }
        }

        SnackbarHost(
            modifier = Modifier.align(Alignment.TopCenter),
            hostState = snackState
        ) { snackbarData: SnackbarData ->
            CustomSnackBar(
                drawableRes = if (snackbarData.visuals.message.startsWith("Gagal")) R.drawable.baseline_error_outline_24 else R.drawable.baseline_check_circle_outline_24,
                message = snackbarData.visuals.message,
                containerColor = if (snackbarData.visuals.message.startsWith("Gagal")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            )
        }

        if (showLogoutDialog) {
            AlertDialog(
                containerColor = Color.White,
                onDismissRequest = { showLogoutDialog = false },
                title = { Text(text = "Konfirmasi Logout") },
                text = { Text("Apakah Anda yakin ingin keluar?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            viewModel.logout()
                            redirectToWelcome("Anda Telah Keluar Akun")
                        }
                    ) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}