package com.project.labs.nutrigrow.ui.screen.validation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.data.model.IndicatorResult
import com.project.labs.nutrigrow.data.model.ZScoreCheckModel
import com.project.labs.nutrigrow.data.model.ZScoreSample
import com.project.labs.nutrigrow.ui.component.button.NutriButton
import com.project.labs.nutrigrow.ui.component.chip.StatusChip
import com.project.labs.nutrigrow.ui.component.input.NutriTextField
import com.project.labs.nutrigrow.ui.component.respond.ErrorMessage
import com.project.labs.nutrigrow.ui.component.respond.LoadingIndicator
import com.project.labs.nutrigrow.ui.state.UiState
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextSecondary
import com.project.labs.nutrigrow.utils.parseMeasurement

private const val FIELD_AGE = "age"
private const val FIELD_WEIGHT = "weight"
private const val FIELD_HEIGHT = "height"

@Composable
fun ValidationCalculatorTab(
    viewModel: ValidationViewModel,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.manual.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 15.dp),
    ) {
        item { Spacer(modifier = Modifier.height(Spacing.xs)) }
        item { CalculatorIntro() }
        item { CalculatorCard(viewModel = viewModel, state = state) }
        item { Spacer(modifier = Modifier.height(Spacing.xxl)) }
    }
}

@Composable
private fun CalculatorIntro() {
    ElevatedCard(
        colors = CardDefaults.cardColors(SurfaceCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Hitung Satu Data",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Memakai perhitungan gizi yang sama dengan yang dipakai kader saat " +
                    "menyimpan penimbangan, tanpa membuat data balita apa pun.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun CalculatorCard(
    viewModel: ValidationViewModel,
    state: UiState<ZScoreCheckModel>?,
) {
    val form by viewModel.form
    val fieldErrors by viewModel.fieldErrors

    ElevatedCard(
        colors = CardDefaults.cardColors(SurfaceCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(
                text = "Jenis Kelamin",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                GenderOption("Laki-laki", form.gender == "M") {
                    viewModel.updateForm(form.copy(gender = "M"))
                }
                GenderOption("Perempuan", form.gender == "F") {
                    viewModel.updateForm(form.copy(gender = "F"))
                }
            }

            Spacer(modifier = Modifier.height(Spacing.sm))
            NutriTextField(
                value = form.age,
                onValueChange = {
                    viewModel.updateForm(form.copy(age = it))
                    viewModel.clearFieldError(FIELD_AGE)
                },
                label = "Umur (bulan)",
                placeholder = "0 - 60",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = fieldErrors.containsKey(FIELD_AGE),
                errorMessage = fieldErrors[FIELD_AGE],
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            NutriTextField(
                value = form.weight,
                onValueChange = {
                    viewModel.updateForm(form.copy(weight = it))
                    viewModel.clearFieldError(FIELD_WEIGHT)
                },
                label = "Berat Badan (kg)",
                placeholder = "misalnya 11,2",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = fieldErrors.containsKey(FIELD_WEIGHT),
                errorMessage = fieldErrors[FIELD_WEIGHT],
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            NutriTextField(
                value = form.height,
                onValueChange = {
                    viewModel.updateForm(form.copy(height = it))
                    viewModel.clearFieldError(FIELD_HEIGHT)
                },
                label = "Tinggi Badan (cm)",
                placeholder = "misalnya 82,5",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = fieldErrors.containsKey(FIELD_HEIGHT),
                errorMessage = fieldErrors[FIELD_HEIGHT],
            )

            Spacer(modifier = Modifier.height(Spacing.md))
            NutriButton(
                text = "Hitung Z-Score",
                onClick = {
                    val errors = validateCalculator(form)
                    viewModel.setFieldErrors(errors)
                    if (errors.isEmpty()) {
                        viewModel.calculate(
                            ZScoreSample(
                                gender = form.gender,
                                age = form.age.trim().toInt(),
                                weight = parseMeasurement(form.weight) ?: 0.0,
                                height = parseMeasurement(form.height) ?: 0.0,
                                label = "manual",
                            )
                        )
                    }
                },
            )

            when (state) {
                is UiState.Loading -> {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    LoadingIndicator()
                }
                is UiState.Success -> {
                    Spacer(modifier = Modifier.height(Spacing.md))
                    IndicatorRow("BB/U — Berat menurut Umur", state.data.wfa)
                    IndicatorRow("TB/U — Tinggi menurut Umur", state.data.hfa)
                    IndicatorRow("BB/TB — Berat menurut Tinggi", state.data.wfh)
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

private fun validateCalculator(form: CalculatorForm): Map<String, String> {
    val errors = mutableMapOf<String, String>()

    val ageValue = form.age.trim().toIntOrNull()
    when {
        form.age.isBlank() -> errors[FIELD_AGE] = "Umur belum diisi."
        ageValue == null -> errors[FIELD_AGE] = "Umur harus berupa angka bulat dalam bulan."
        ageValue < 0 || ageValue > 60 -> errors[FIELD_AGE] = "Umur yang dipantau 0 sampai 60 bulan."
    }

    val weightValue = parseMeasurement(form.weight)
    when {
        form.weight.isBlank() -> errors[FIELD_WEIGHT] = "Berat badan belum diisi."
        weightValue == null -> errors[FIELD_WEIGHT] = "Berat badan harus berupa angka, misalnya 11,2."
        weightValue <= 0 -> errors[FIELD_WEIGHT] = "Berat badan harus lebih besar dari nol."
    }

    val heightValue = parseMeasurement(form.height)
    when {
        form.height.isBlank() -> errors[FIELD_HEIGHT] = "Tinggi badan belum diisi."
        heightValue == null -> errors[FIELD_HEIGHT] = "Tinggi badan harus berupa angka, misalnya 82,5."
        heightValue <= 0 -> errors[FIELD_HEIGHT] = "Tinggi badan harus lebih besar dari nol."
    }

    return errors
}

@Composable
private fun GenderOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        color = if (selected) Color.White else TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(Spacing.lg))
            .background(if (selected) BrandGreen else BrandGreenSoft)
            .clickable { onClick() }
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    )
}

@Composable
private fun IndicatorRow(title: String, indicator: IndicatorResult) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = Spacing.sm)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "z = ${formatZ(indicator.z_score)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
            StatusChip(label = indicator.status, color = bandColor(indicator.z_score))
        }

        AnimatedVisibility(visible = expanded) {
            ReferenceTable(indicator)
        }

        Text(
            text = if (expanded) "Sembunyikan tabel WHO" else "Lihat tabel WHO",
            style = MaterialTheme.typography.bodySmall,
            color = BrandGreen,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(top = Spacing.xs),
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            modifier = Modifier.padding(top = Spacing.xs),
        )
    }
}
