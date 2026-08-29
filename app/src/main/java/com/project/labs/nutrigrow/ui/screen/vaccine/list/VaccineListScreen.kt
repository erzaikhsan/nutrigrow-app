package com.project.labs.nutrigrow.ui.screen.vaccine.list

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextPrimary
import com.project.labs.nutrigrow.activity.vaccine.AddVaccineActivity
import com.project.labs.nutrigrow.activity.vaccine.UpdateVaccineActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.ui.component.card.VaccineCard
import com.project.labs.nutrigrow.ui.component.input.SearchCard
import com.project.labs.nutrigrow.ui.component.pagination.PaginationBar
import com.project.labs.nutrigrow.ui.component.pagination.rememberPagination
import com.project.labs.nutrigrow.ui.component.respond.EmptyState
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.screen.ViewModelFactory
import com.project.labs.nutrigrow.ui.screen.child.ChildProfileViewModel
import com.project.labs.nutrigrow.ui.screen.child.formatDate
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft

@Composable
fun VaccineListScreen(
    id: String,
    redirectToHome: (String) -> Unit,
    viewModel: ChildProfileViewModel = viewModel(
        factory = ViewModelFactory.getInstance(LocalContext.current)
    ),
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val checkAuth by viewModel.isAuthenticated
    var keyword by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.checkAuthentication()
    }

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getVaccineByChildId(id)
        }
    }

    val isOfficer = (checkAuth as? UiState.Success<AuthModel>)?.data?.role == "Officer"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft)
    ) {
        viewModel.vaccine.collectAsState(initial = UiState.Loading).value.let { vaccines ->
            when (vaccines) {
                is UiState.Loading -> {
                    LoadingIndicator()
                    LaunchedEffect(Unit) {
                        viewModel.getVaccineByChildId(id)
                    }
                }
                is UiState.Success -> {
                    val filtered = remember(vaccines.data, keyword) {
                        if (keyword.isBlank()) {
                            vaccines.data
                        } else {
                            vaccines.data.filter { item ->
                                item.vaccine_name.contains(keyword, ignoreCase = true) ||
                                    item.place.contains(keyword, ignoreCase = true)
                            }
                        }
                    }
                    val pagination = rememberPagination(items = filtered, listState = listState)

                    Column(modifier = Modifier.fillMaxSize()) {
                        SearchCard(
                            value = keyword,
                            onValueChange = { keyword = it },
                            placeholder = "Cari Nama Vaksin",
                        )
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
                                text = "Riwayat Imunisasi",
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
                                .padding(horizontal = 15.dp),
                            color = BrandGreen,
                            thickness = 3.dp
                        )
                        LazyColumn(
                            state = listState,
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 15.dp)
                        ) {
                            item { Spacer(modifier = Modifier.height(1.dp)) }

                            if (pagination.visibleItems.isEmpty()) {
                                item {
                                    EmptyState(
                                        title = if (keyword.isBlank()) {
                                            "Belum ada imunisasi"
                                        } else {
                                            "Imunisasi tidak ditemukan"
                                        },
                                        description = if (keyword.isBlank()) {
                                            "Balita ini belum memiliki catatan imunisasi."
                                        } else {
                                            "Tidak ada imunisasi yang cocok dengan \"$keyword\"."
                                        },
                                    )
                                }
                            } else {
                                items(pagination.visibleItems.size) { index ->
                                    val item = pagination.visibleItems[index]
                                    VaccineCard(
                                        vaccineName = item.vaccine_name,
                                        dateLabel = formatDate(item.date),
                                        place = item.place,
                                        onClick = if (isOfficer) {
                                            {
                                                activityLauncher.launch(
                                                    Intent(context, UpdateVaccineActivity::class.java).apply {
                                                        putExtra("id", item.id)
                                                        putExtra("childId", id)
                                                    }
                                                )
                                            }
                                        } else {
                                            null
                                        },
                                    )
                                }
                                item { PaginationBar(pagination = pagination, itemLabel = "imunisasi") }
                            }

                            item { Spacer(modifier = Modifier.height(70.dp)) }
                        }
                    }
                }
                is UiState.Error -> {
                    ErrorMessage(message = vaccines.errorMessage)
                }
                is UiState.Unauthorized -> {
                    redirectToHome("Sesi Telah Berakhir\nSilahkan Masuk Kembali")
                }
                else -> { }
            }
        }

        if (isOfficer) {
            FloatingActionButton(
                shape = CircleShape,
                containerColor = SurfaceCard,
                contentColor = TextPrimary,
                onClick = {
                    activityLauncher.launch(
                        Intent(context, AddVaccineActivity::class.java).apply {
                            putExtra("id", id)
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Tambah Imunisasi",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(end = 5.dp)
                    )
                }
            }
        }
    }
}
