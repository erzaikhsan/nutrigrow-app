package com.project.labs.nutrigrow.ui.screen.validation

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.project.labs.nutrigrow.data.model.ZScoreCheckModel
import com.project.labs.nutrigrow.ui.component.button.NutriButton
import com.project.labs.nutrigrow.ui.component.button.NutriOutlinedButton
import com.project.labs.nutrigrow.ui.component.chip.StatusChip
import com.project.labs.nutrigrow.ui.component.respond.EmptyState
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.state.StatusNormal
import com.project.labs.nutrigrow.ui.state.StatusSedang
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextMuted
import com.project.labs.nutrigrow.ui.theme.TextSecondary
import java.io.File

@Composable
fun ValidationSampleTab(
    viewModel: ValidationViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val sampleSet by viewModel.sampleSet
    val importError by viewModel.importError
    val state by viewModel.samples.collectAsState()

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            try {
                viewModel.applySampleSet(importSampleSet(context, uri))
            } catch (e: Exception) {
                viewModel.failImport(e.message ?: "Berkas tidak dapat dibaca.")
            }
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
    ) {
        item { Spacer(modifier = Modifier.height(Spacing.xs)) }

        item {
            ImportCard(
                sampleSet = sampleSet,
                importError = importError,
                onImport = { importLauncher.launch(SPREADSHEET_MIME_TYPES) },
                onClear = { viewModel.clearSampleSet() },
            )
        }

        if (sampleSet == null) {
            item {
                EmptyState(
                    title = "Belum ada berkas uji",
                    description = "Pilih berkas Excel atau CSV berisi sampel, lalu jalankan ujinya di sini.",
                )
            }
        } else {
            item {
                SampleRunCard(
                    sampleSet = sampleSet!!,
                    state = state,
                    onRun = { viewModel.runSamples(sampleSet!!.samples.map { it.toRequest() }) },
                    context = context,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xxl)) }
    }
}

@Composable
private fun ImportCard(
    sampleSet: ValidationSampleSet?,
    importError: String?,
    onImport: () -> Unit,
    onClear: () -> Unit,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(SurfaceCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Berkas Uji",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Unggah berkas Excel berisi sampel uji. Berkas harus memuat kolom jenis " +
                    "kelamin, umur, berat badan, tinggi badan, indeks, dan z-score referensi.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )

            if (sampleSet != null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = "${sampleSet.source} · ${sampleSet.samples.size} baris terbaca",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
            }

            if (importError != null) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = importError,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))
            NutriButton(
                text = if (sampleSet == null) "Pilih Berkas Excel" else "Ganti Berkas",
                onClick = onImport,
            )

            if (sampleSet != null) {
                Spacer(modifier = Modifier.height(Spacing.xs))
                Text(
                    text = "Hapus berkas",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onClear() }
                        .padding(vertical = Spacing.xs),
                )
            }
        }
    }
}

@Composable
private fun SampleRunCard(
    sampleSet: ValidationSampleSet,
    state: UiState<List<ZScoreCheckModel>>,
    onRun: () -> Unit,
    context: Context,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(SurfaceCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Jalankan Uji",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Setiap baris dihitung ulang oleh aplikasi, lalu dibandingkan dengan " +
                    "klasifikasi rujukan pada berkas uji.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(Spacing.md))

            NutriButton(
                text = "Jalankan ${sampleSet.samples.size} Sampel",
                onClick = onRun,
            )

            when (state) {
                is UiState.Loading -> { }
                is UiState.Success -> {
                    val rows = sampleSet.samples.map { sample ->
                        ValidationRow(
                            sample = sample,
                            result = state.data.firstOrNull { it.label == sample.no.toString() },
                        )
                    }
                    val summary = ValidationSummary(
                        total = rows.size,
                        matched = rows.count { it.matched },
                    )

                    Spacer(modifier = Modifier.height(Spacing.md))
                    SummaryStrip(summary)
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    rows.forEach { SampleRowItem(it) }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    NutriOutlinedButton(
                        text = "Ekspor Hasil (CSV)",
                        onClick = { exportCsv(context, rows, summary) },
                    )
                }
                is UiState.Error -> {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    ErrorMessage(message = state.errorMessage)
                }
                else -> { }
            }
        }
    }
}

@Composable
private fun SummaryStrip(summary: ValidationSummary) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.sm))
            .background(BrandGreenSoft)
            .padding(Spacing.md),
    ) {
        Text(
            text = "${summary.matched}/${summary.total}",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.width(Spacing.md))
        Column {
            Text(
                text = "klasifikasi cocok",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = String.format("%.1f%% dari sampel", summary.percent).replace('.', ','),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun SampleRowItem(row: ValidationRow) {
    var expanded by remember { mutableStateOf(false) }
    val indicator = row.result?.indicatorOf(row.sample.index)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = Spacing.xs)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = row.sample.no.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                modifier = Modifier.width(24.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${row.sample.index} · ${if (row.sample.gender == "M") "Laki-laki" else "Perempuan"} · ${row.sample.age} bln",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "rujukan ${formatZ(row.sample.ref_z)} · aplikasi ${formatZ(row.appZScore)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            StatusChip(
                label = if (row.matched) "cocok" else "beda",
                color = if (row.matched) StatusNormal else StatusSedang,
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(start = 24.dp, top = Spacing.xs)) {
                Text(
                    text = "Rujukan: ${row.sample.ref_label}   ·   Aplikasi: ${row.appLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Text(
                    text = "BB ${formatNumber(row.sample.weight)} kg · TB ${formatNumber(row.sample.height)} cm · selisih z ${formatZ(row.delta)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                )
                if (indicator != null) {
                    ReferenceTable(indicator)
                }
            }
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            modifier = Modifier.padding(top = Spacing.xs),
        )
    }
}

private fun exportCsv(
    context: Context,
    rows: List<ValidationRow>,
    summary: ValidationSummary,
) {
    try {
        val file = File(context.getExternalFilesDir(null), "validasi-zscore.csv")
        file.bufferedWriter().use { writer ->
            writer.appendLine("No;Jenis Kelamin;Umur (bln);BB (kg);TB (cm);Indeks;z Rujukan;z Aplikasi;Selisih;Klasifikasi Rujukan;Klasifikasi Aplikasi;Status")
            rows.forEach { row ->
                writer.appendLine(
                    listOf(
                        row.sample.no,
                        if (row.sample.gender == "M") "Laki-laki" else "Perempuan",
                        row.sample.age,
                        formatNumber(row.sample.weight),
                        formatNumber(row.sample.height),
                        row.sample.index,
                        formatZ(row.sample.ref_z),
                        formatZ(row.appZScore),
                        formatZ(row.delta),
                        row.sample.ref_label,
                        row.appLabel,
                        if (row.matched) "cocok" else "beda",
                    ).joinToString(";")
                )
            }
            writer.appendLine()
            writer.appendLine("Klasifikasi cocok;${summary.matched};dari;${summary.total}")
        }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Bagikan hasil validasi"))
    } catch (e: Exception) {
        Toast.makeText(context, "Gagal menyimpan berkas: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
