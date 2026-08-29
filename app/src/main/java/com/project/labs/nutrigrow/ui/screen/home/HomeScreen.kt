package com.project.labs.nutrigrow.ui.screen.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextPrimary
import com.project.labs.nutrigrow.R
import com.project.labs.nutrigrow.activity.officer.AddOfficerActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private const val SEARCH_DEBOUNCE_MS = 400L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    redirectToWelcome: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val now = ZonedDateTime.now(ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
    val date = now.format(formatter)

    val checkAuth by viewModel.isAuthenticated

    var keyword by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    LaunchedEffect(keyword) {
        if (keyword.isNotBlank()) delay(SEARCH_DEBOUNCE_MS)
        when (checkAuth) {
            is UiState.Success -> {
                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin") {
                    viewModel.getOfficerByName(keyword)
                } else {
                    viewModel.getParentByNameAndRegion(keyword, (checkAuth as UiState.Success<AuthModel>).data.region)
                }
            }
            else -> { }
        }
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            when (checkAuth) {
                is UiState.Success -> {
                    when((checkAuth as UiState.Success<AuthModel>).data.role) {
                        "Admin" -> {
                            viewModel.getOfficers()
                        }
                        "Officer" -> {
                            viewModel.getParentByRegion((checkAuth as UiState.Success<AuthModel>).data.region)
                        }
                        "Parent" -> {
                            viewModel.getChildrenByParent((checkAuth as UiState.Success<AuthModel>).data.id)
                            viewModel.getIncomingEvent(date, (checkAuth as UiState.Success<AuthModel>).data.region)
                        }
                        else -> {}
                    }
                }
                else -> {}
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE0FFD2))
    ) {
        viewModel.user.collectAsState(initial = UiState.Loading).value.let { user ->
            when (user) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    when (checkAuth) {
                        is UiState.Success -> {
                            LaunchedEffect(Unit) {
                                viewModel.getUserProfile((checkAuth as UiState.Success<AuthModel>).data.role)
                            }
                        }
                        else -> {}
                    }
                }
                is UiState.Success -> {
                    when (checkAuth) {
                        is UiState.Success -> {
                            when((checkAuth as UiState.Success<AuthModel>).data.role){
                                "Admin" -> {
                                    HomeAdminContent(
                                        user = user.data,
                                        auth = (checkAuth as UiState.Success<AuthModel>).data,
                                        viewModel = viewModel,
                                        context = context,
                                        activityLauncher = activityLauncher,
                                        keyword = keyword,
                                        onKeywordChange = { keyword = it },
                                    )
                                }
                                "Officer" -> {
                                    HomeOfficerContent(
                                        user = user.data,
                                        auth = (checkAuth as UiState.Success<AuthModel>).data,
                                        viewModel = viewModel,
                                        context = context,
                                        activityLauncher = activityLauncher,
                                        keyword = keyword,
                                        onKeywordChange = { keyword = it },
                                    )
                                }
                                "Parent" -> {
                                    HomeParentContent(
                                        user = user.data,
                                        auth = (checkAuth as UiState.Success<AuthModel>).data,
                                        viewModel = viewModel,
                                        context = context,
                                        activityLauncher = activityLauncher,
                                        date = date,
                                    )
                                }
                                else -> { }
                            }
                        }
                        else -> { }
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = user.errorMessage)
                }
                is UiState.Unauthorized -> {
                    redirectToWelcome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                }

                else -> {}
            }
        }

        when (checkAuth) {
            is UiState.Success -> {
                if ((checkAuth as UiState.Success<AuthModel>).data.role == "Admin") {
                    FloatingActionButton(
                        shape = CircleShape,
                        containerColor = SurfaceCard,
                        contentColor = TextPrimary,
                        onClick = {
                            activityLauncher.launch(
                                Intent(
                                    context,
                                    AddOfficerActivity::class.java
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
                                text = "Tambah Kader ",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.W600,
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.round_add_circle_24),
                                contentDescription = "Tambah kader",
                                modifier = Modifier
                                    .size(30.dp)
                                    .padding(
                                        bottom = 5.dp
                                    ),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
            else -> { }
        }
    }
}
