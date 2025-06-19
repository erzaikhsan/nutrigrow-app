package com.project.labs.nutrigrow.ui.screen.event.detail

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.event.UpdateEventActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.EventModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.component.snackbar.CustomSnackBar
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DetailEventScreen(
    id: String,
    redirectToHome: () -> Unit,
    viewModel: DetailEventViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val snackState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }

    val checkAuth by viewModel.isAuthenticated
    val delete: UiState<EventModel> by viewModel.delete

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getEventById(id)
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        return zonedDateTime.format(outputFormatter)
    }

    DisposableEffect( key1 = delete){
        when (delete) {
            is UiState.Loading -> { }

            is UiState.Success -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Berhasil Menghapus Data Kegiatan")
                    redirectToHome()
                }

            }

            is UiState.Error -> {
                coroutineScope.launch {
                    snackState.showSnackbar("Gagal Menghapus Data Kegiatan\n${(delete as UiState.Error).errorMessage}")
                }
            }
            else -> {}
        }

        onDispose { }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        viewModel.event.collectAsState(initial = UiState.Loading).value.let { events ->
            when (events) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    viewModel.getEventById(id)
                }
                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        item {
                            AsyncImage(
                                model = R.drawable.posyandu,
                                contentDescription = "$id Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                                alignment = Alignment.CenterStart,
                            )
                        }
                        item {
                            ElevatedCard(
                                colors = CardDefaults.cardColors(Color(0xFFE0FFD2)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = CardDefaults.shape)
                                    .shadow(
                                        elevation = 0.dp,
                                        spotColor = Color.Transparent,
                                        shape = CardDefaults.shape
                                    )
                                    .padding(
                                    start = 15.dp,
                                    end = 15.dp,
                                    top = 20.dp,
                                    bottom = 20.dp
                                )
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF00BF63))
                                        .padding(
                                            horizontal = 20.dp,
                                            vertical = 10.dp
                                        )
                                ) {
                                    Text(
                                        text = events.data.title.toUpperCase(Locale.getDefault()),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontSize = 17.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 20.dp,
                                            vertical = 10.dp
                                        )
                                ) {
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Text(
                                            text = "Tanggal :",
                                            maxLines = 1,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            text = formatDate(events.data.date),
                                            maxLines = 1,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Text(
                                            text = "Tempat :",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            text = events.data.place,
                                            maxLines = 1,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ){
                                        Text(
                                            text = "Waktu :",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                        Text(
                                            text = "${events.data.start_time} - ${events.data.end_time} WIB",
                                            maxLines = 1,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Deskripsi Kegiatan :",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    Text(
                                        text = events.data.description,
                                        textAlign = TextAlign.Justify,
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(75.dp))
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = events.errorMessage)
                }
                is UiState.Unauthorized -> {
                    redirectToHome()
                }
            }
        }

        when (checkAuth) {
            is UiState.Success -> {
                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin"){
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        FloatingActionButton(
                            shape = CircleShape,
                            containerColor = Color(0xFF2C98EE),
                            onClick = {
                                activityLauncher.launch(
                                    Intent(
                                        context,
                                        UpdateEventActivity::class.java
                                    ).apply {
                                        putExtra(
                                            "id",
                                            id
                                        )
                                    }
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ){
                                Text(
                                    text = "Ubah Kegiatan",
                                    fontSize = 17.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.W600,
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        FloatingActionButton(
                            shape = CircleShape,
                            containerColor = Color(0xFFE74C3C),
                            onClick = {
                                showDeleteDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ){
                                Text(
                                    text = "Hapus Kegiatan",
                                    fontSize = 17.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.W600,
                                    modifier = Modifier.padding(end = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
            else -> { }
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

        if (showDeleteDialog) {
            AlertDialog(
                containerColor = Color.White,
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(text = "Konfirmasi Hapus") },
                text = { Text("Apakah Anda yakin ingin menghapus kegiatan?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteEvent(id)
                        }
                    ) {
                        Text("Ya")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}