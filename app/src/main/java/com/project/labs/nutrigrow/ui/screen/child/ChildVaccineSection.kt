package com.project.labs.nutrigrow.ui.screen.child

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.activity.vaccine.AddVaccineActivity
import com.project.labs.nutrigrow.activity.vaccine.UpdateVaccineActivity
import com.project.labs.nutrigrow.activity.vaccine.VaccineListActivity
import com.project.labs.nutrigrow.data.model.AuthModel
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.ui.component.card.VaccineCard
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.OriginalLoading
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing

private const val VACCINE_PREVIEW_COUNT = 3

@Composable
fun ChildVaccineSection(
    child: UiState.Success<ChildrenModel>,
    checkAuth: UiState<AuthModel>,
    id: String,
    context: Context,
    activityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    viewModel: ChildProfileViewModel,
) {
    val openVaccineList = {
        activityLauncher.launch(
            Intent(context, VaccineListActivity::class.java).apply {
                putExtra("id", child.data.children_id)
            }
        )
    }

    ElevatedCard(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .padding(bottom = 15.dp, top = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 15.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Text(
                    text = "Imunisasi Balita",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if ((checkAuth as? UiState.Success<AuthModel>)?.data?.role == "Officer") {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah imunisasi",
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    activityLauncher.launch(
                                        Intent(context, AddVaccineActivity::class.java).apply {
                                            putExtra("id", child.data.children_id)
                                        }
                                    )
                                },
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.size(Spacing.sm))
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Lihat semua imunisasi",
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { openVaccineList() },
                        tint = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            viewModel.vaccine.collectAsState(initial = UiState.Loading).value.let { vaccines ->
                when (vaccines) {
                    is UiState.Loading -> {
                        OriginalLoading()
                        LaunchedEffect(Unit) {
                            viewModel.getVaccineByChildId(id)
                        }
                    }
                    is UiState.Success -> {
                        if (vaccines.data.isEmpty()) {
                            EmptyVaccineCard()
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                vaccines.data.take(VACCINE_PREVIEW_COUNT).forEach { vaccineItem ->
                                    VaccineCard(
                                        vaccineName = vaccineItem.vaccine_name,
                                        dateLabel = formatDate(vaccineItem.date),
                                        place = vaccineItem.place,
                                        onClick = {
                                            if ((checkAuth as? UiState.Success<AuthModel>)?.data?.role == "Officer") {
                                                activityLauncher.launch(
                                                    Intent(context, UpdateVaccineActivity::class.java).apply {
                                                        putExtra("id", vaccineItem.id)
                                                        putExtra("childId", id)
                                                    }
                                                )
                                            } else {
                                                openVaccineList()
                                            }
                                        },
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                if (vaccines.data.size > VACCINE_PREVIEW_COUNT) {
                                    Text(
                                        text = "Selengkapnya (${vaccines.data.size} imunisasi)",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.W600,
                                        color = BrandGreen,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(CardDefaults.shape)
                                            .clickable { openVaccineList() }
                                            .padding(vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                    is UiState.Error -> {
                        ErrorMessage(message = vaccines.errorMessage)
                    }

                    else -> { }
                }
            }
        }
    }
}

@Composable
private fun EmptyVaccineCard() {
    ElevatedCard(
        colors = CardDefaults.cardColors(BrandGreenSoft),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .padding(vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF9800))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Balita belum mendapatkan Imunisasi",
                fontSize = 17.sp,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Silahkan datang ke Posyandu Desa Jipang atau Puskesmas terdekat untuk mendapatkan informasi lebih lanjut mengenai imunisasi anak.",
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.W500,
            )
        }
    }
}
