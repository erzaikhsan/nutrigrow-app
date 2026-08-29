package com.project.labs.nutrigrow.ui.screen.child.list

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.activity.child.ChildActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.list.ChildListColumn
import com.project.labs.nutrigrow.ui.component.input.SearchCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import kotlinx.coroutines.delay

private const val SEARCH_DEBOUNCE_MS = 400L

@Composable
fun ChildrenScreen(
    redirectToWelcome: (String) -> Unit,
    viewModel: ChildrenViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val activity = LocalContext.current as Activity

    val checkAuth by viewModel.isAuthenticated
    var keyword by remember { mutableStateOf("") }
    var showGraduateDialog by remember { mutableStateOf(false) }
    val graduateState by viewModel.graduate
    val listState = rememberLazyListState()

    LaunchedEffect(graduateState) {
        if (graduateState is UiState.Success) {
            viewModel.getUserProfile()
            viewModel.resetGraduateState()
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.checkAuthentication()
    }

    LaunchedEffect(keyword) {
        if (keyword.isNotBlank()) delay(SEARCH_DEBOUNCE_MS)
        when (checkAuth) {
            is UiState.Success -> {
                val auth = (checkAuth as UiState.Success<AuthModel>).data
                when (auth.role) {
                    "Officer" -> viewModel.getChildrenByNameAndRegion(keyword, auth.region)
                    "Parent" -> viewModel.getChildrenByParent(auth.id)
                    else -> viewModel.getChildrenByName(keyword)
                }
            }
            else -> { }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            viewModel.user.collectAsState(initial = UiState.Loading).value.let { users ->
                when (users) {
                    is UiState.Loading -> {
                        LoadingIndicator()
                        LaunchedEffect(Unit) {
                            viewModel.getUserProfile()
                        }
                    }
                    is UiState.Success -> {
                        val authRole = (checkAuth as? UiState.Success<AuthModel>)?.data?.role

                        if (authRole != null && authRole != "Parent") {
                            Spacer(modifier = Modifier.height(10.dp))
                            SearchCard(
                                value = keyword,
                                onValueChange = { keyword = it },
                                placeholder = "Cari Nama Balita",
                            )
                        }

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
                                .padding(horizontal = 15.dp)
                        ) {
                            Text(
                                text = when (authRole) {
                                    "Officer" -> "Daftar Balita ${
                                        when (users.data.region) {
                                            "RW1" -> "PAMUJI 1"
                                            "RW2" -> "PAMUJI 2"
                                            "RW3" -> "PAMUJI 3"
                                            "RW4" -> "PAMUJI 4"
                                            "RW5" -> "PAMUJI 5"
                                            else -> "PAMUJI"
                                        }
                                    }"
                                    "Admin" -> "Balita Posyandu Jipang"
                                    else -> "Daftar Balita"
                                },
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 25.dp, end = 25.dp, top = 8.dp, bottom = 8.dp)
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 15.dp, end = 15.dp),
                            color = BrandGreen,
                            thickness = 3.dp
                        )

                        if (authRole != null && authRole != "Parent") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = { showGraduateDialog = true },
                                colors = ButtonDefaults.buttonColors(BrandGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 15.dp)
                            ) {
                                Text(
                                    text = "Luluskan Balita Lewat Umur",
                                    color = Color.White,
                                    fontWeight = FontWeight.W500,
                                )
                            }
                        }

                        if (showGraduateDialog) {
                            AlertDialog(
                                containerColor = Color.White,
                                onDismissRequest = { showGraduateDialog = false },
                                title = { Text(text = "Luluskan Balita Lewat Umur") },
                                text = { Text("Seluruh balita yang usianya telah melewati batas pemantauan akan ditandai lulus sekaligus. Lanjutkan?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showGraduateDialog = false
                                            viewModel.graduateChildren()
                                        }
                                    ) {
                                        Text("Ya, Luluskan")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showGraduateDialog = false }) {
                                        Text("Batal")
                                    }
                                }
                            )
                        }

                        val searchResult = viewModel.search.collectAsState(initial = UiState.Loading).value
                        val allChildren = viewModel.children.collectAsState(initial = UiState.Loading).value
                        val shown = if (searchResult is UiState.Success) searchResult else allChildren

                        when (shown) {
                            is UiState.Loading -> {
                                LoadingIndicator()
                                LaunchedEffect(authRole) {
                                    when (authRole) {
                                        null -> { }
                                        "Officer" -> viewModel.getChildrenByRegion(users.data.region)
                                        else -> viewModel.getAllChildren()
                                    }
                                }
                            }
                            is UiState.Success -> {
                                ChildListColumn(
                                    children = shown.data,
                                    keyword = keyword,
                                    listState = listState,
                                    onOpen = { child ->
                                        activity.startActivity(
                                            Intent(context, ChildActivity::class.java).apply {
                                                putExtra("id", child.children_id)
                                                authRole?.let { putExtra("role", it) }
                                            }
                                        )
                                    },
                                )
                            }
                            is UiState.Error -> {
                                ErrorMessage(message = shown.errorMessage)
                            }
                            else -> { }
                        }
                    }

                    is UiState.Error -> {
                        ErrorMessage(message = users.errorMessage)
                    }

                    is UiState.Unauthorized -> {
                        redirectToWelcome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                    }

                    else -> { }
                }
            }
        }
    }
}
