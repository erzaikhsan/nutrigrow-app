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
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.activity.growth.DetailGrowthActivity
import com.project.labs.nutrigrow.data.model.ChildrenModel
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.ui.component.card.AlertBanner
import com.project.labs.nutrigrow.ui.component.card.MeasurementCard
import com.project.labs.nutrigrow.ui.component.respond.EmptyState
import com.project.labs.nutrigrow.ui.component.respond.OriginalLoading
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.state.StatusBerat
import com.project.labs.nutrigrow.ui.state.StatusRingan
import com.project.labs.nutrigrow.ui.state.gainColor
import com.project.labs.nutrigrow.ui.state.gainLabel
import com.project.labs.nutrigrow.ui.state.hcaColor
import com.project.labs.nutrigrow.ui.state.hcaLabel
import com.project.labs.nutrigrow.ui.state.hfaAdvice
import com.project.labs.nutrigrow.ui.state.hfaColor
import com.project.labs.nutrigrow.ui.state.hfaLabel
import com.project.labs.nutrigrow.ui.state.muacColor
import com.project.labs.nutrigrow.ui.state.muacLabel
import com.project.labs.nutrigrow.ui.state.wfaAdvice
import com.project.labs.nutrigrow.ui.state.wfaColor
import com.project.labs.nutrigrow.ui.state.wfaLabel
import com.project.labs.nutrigrow.ui.state.wfhAdvice
import com.project.labs.nutrigrow.ui.state.wfhColor
import com.project.labs.nutrigrow.ui.state.wfhLabel
import com.project.labs.nutrigrow.ui.theme.Spacing

@Composable
fun ChildGrowthSection(
    child: UiState.Success<ChildrenModel>,
    id: String,
    context: Context,
    activityLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    viewModel: ChildProfileViewModel,
) {
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
            .padding(bottom = 5.dp, top = 5.dp, start = 10.dp, end = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp)
            ) {
                Text(
                    text = "Pertumbuhan Balita",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Lihat Semua",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            activityLauncher.launch(
                                Intent(context, DetailGrowthActivity::class.java).apply {
                                    putExtra("id", child.data.children_id)
                                }
                            )
                        },
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(Spacing.sm))

            viewModel.growth.collectAsState(initial = UiState.Loading).value.let { growth ->
                when (growth) {
                    is UiState.Loading -> {
                        OriginalLoading()
                        LaunchedEffect(Unit) {
                            viewModel.getLastGrowth(id)
                        }
                    }

                    is UiState.Success -> {
                        LastUpdatedRow(date = growth.data.date)
                        GrowthMeasurements(growth = growth.data)
                    }

                    is UiState.Error -> {
                        EmptyState(
                            title = "Tidak Ada Data Pertumbuhan Balita",
                            description = "Silahkan masukan data pertumbuhan anak secara rutin setiap bulannya pada Posyandu Balita di Desa Jipang.",
                        )
                    }

                    else -> { }
                }
            }
        }
    }
}

@Composable
private fun LastUpdatedRow(date: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, bottom = Spacing.sm)
    ) {
        Text(
            text = "Terakhir diperbarui :",
            fontSize = 14.sp,
            fontWeight = FontWeight.W500,
        )
        Text(
            text = formatDate(date),
            fontSize = 14.sp,
            fontStyle = FontStyle.Italic,
        )
    }
}

@Composable
private fun GrowthMeasurements(growth: GrowthModel) {
    if (growth.needs_referral) {
        AlertBanner(
            title = "Perlu Dirujuk",
            message = "Berat badan tidak naik ${growth.consecutive_no_gain} kali penimbangan berturut-turut. " +
                "Segera bawa balita ke tenaga kesehatan untuk pemeriksaan lebih lanjut.",
            color = StatusBerat,
        )
    }

    if (growth.is_flagged && growth.flag_reason.isNotBlank()) {
        AlertBanner(
            title = "Perlu Diperiksa Ulang",
            message = growth.flag_reason,
            color = StatusRingan,
        )
    }

    MeasurementCard(
        label = "Berat Badan",
        value = "${growth.weight} kg",
        statusLabel = wfaLabel(growth.wfa_status),
        statusColor = wfaColor(growth.wfa_status),
        zScore = growth.wfa_zscore,
        advice = wfaAdvice(growth.wfa_status),
    )

    MeasurementCard(
        label = "Tinggi Badan",
        value = "${growth.height} Cm",
        statusLabel = hfaLabel(growth.hfa_status),
        statusColor = hfaColor(growth.hfa_status),
        zScore = growth.hfa_zscore,
        advice = hfaAdvice(growth.hfa_status),
    )

    MeasurementCard(
        label = "Berat Menurut Tinggi",
        value = "${growth.weight} kg / ${growth.height} Cm",
        statusLabel = wfhLabel(growth.wfh_status),
        statusColor = wfhColor(growth.wfh_status),
        zScore = growth.wfh_zscore,
        advice = wfhAdvice(growth.wfh_status),
    )

    MeasurementCard(
        label = "Lingkar Kepala",
        value = "${growth.head_circum} Cm",
        statusLabel = hcaLabel(growth.head_circum_status),
        statusColor = hcaColor(growth.head_circum_status),
        zScore = growth.head_circum_zscore,
    )

    MeasurementCard(
        label = "Lingkar Lengan",
        value = "${growth.arm_circum} Cm",
        statusLabel = muacLabel(growth.muac_status),
        statusColor = muacColor(growth.muac_status),
        showZScore = false,
    )

    MeasurementCard(
        label = "Kenaikan Berat Badan",
        value = growth.weight_gain?.let { String.format("%+.2f kg", it) } ?: "-",
        statusLabel = gainLabel(growth.gain_status),
        statusColor = gainColor(growth.gain_status),
        showZScore = false,
    )

    if (growth.note.isNotBlank()) {
        NoteCard(note = growth.note)
    }
}

@Composable
private fun NoteCard(note: String) {
    ElevatedCard(
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Catatan Posyandu :",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
