package com.project.labs.nutrigrow.ui.screen.event

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.activity.event.AddEventActivity
import com.project.labs.nutrigrow.activity.event.DetailEventActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.card.EventCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState

@Composable
fun EventScreen(
    redirectToHome: (String) -> Unit,
    viewModel: EventViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val checkAuth by viewModel.isAuthenticated

    LaunchedEffect(key1 = checkAuth) {
        viewModel.checkAuthentication()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getAllEvent()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0FFD2))
    ) {
        viewModel.event.collectAsState(initial = UiState.Loading).value.let { event ->
            when (event) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    viewModel.getAllEvent()
                }
                is UiState.Success -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 15.dp)
                            .background(Color(0xFFE0FFD2)),
                    ) {
                        when (checkAuth) {
                            is UiState.Success -> {
                                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin") {
                                    item {
                                        Spacer(modifier = Modifier.height(15.dp))
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
                                                text = "Kegiatan Posyandu",
                                                fontSize = 19.sp,
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(
                                                        start = 25.dp,
                                                        end = 25.dp,
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
                                    }
                                } else {
                                    item { Spacer(modifier = Modifier.height(5.dp)) }
                                }
                            }
                                else -> {}
                        }
                        items(event.data.size) { events ->
                            EventCard(
                                title = event.data[events].title,
                                date = event.data[events].date,
                                start_time = event.data[events].start_time,
                                end_time = event.data[events].end_time,
                                place = event.data[events].place,
                                onClick = {
                                    activityLauncher.launch(
                                        Intent(context, DetailEventActivity::class.java).apply {
                                            putExtra("id", event.data[events].id)
                                            putExtra("from_notification", false)
                                        }
                                    )
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(70.dp)) }
                    }
                }

                is UiState.Error -> {
                    ErrorMessage(message = event.errorMessage)
                }

                is UiState.Unauthorized -> {
                    redirectToHome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                }
            }
        }
        when (checkAuth) {
            is UiState.Success -> {
                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin") {
                    FloatingActionButton(
                        shape = CircleShape,
                        onClick = {
                            activityLauncher.launch(
                                Intent(
                                    context,
                                    AddEventActivity::class.java
                                )
                            )
                        },
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding( horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        ){
                            Text(
                                text = "Tambah Kegiatan",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W600,
                                modifier = Modifier.padding(end = 5.dp)
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }
}
