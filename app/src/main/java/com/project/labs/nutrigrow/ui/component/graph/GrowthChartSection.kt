package com.project.labs.nutrigrow.ui.component.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.ui.component.card.AssessmentCard
import com.project.labs.nutrigrow.ui.component.graph.bbtb.WflhChart
import com.project.labs.nutrigrow.ui.component.graph.bbu.WfaChart02
import com.project.labs.nutrigrow.ui.component.graph.bbu.WfaChart25
import com.project.labs.nutrigrow.ui.component.graph.tbu.GrowthChart02
import com.project.labs.nutrigrow.ui.component.graph.tbu.GrowthChart25
import com.project.labs.nutrigrow.ui.component.graph.zscore.ZScoreChart
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.TextSecondary

private const val STYLE_ZSCORE = "zscore"
private const val STYLE_WHO = "who"

private val ZSCORE_LINE_WFA = Color(0xFF1565C0)
private val ZSCORE_LINE_HFA = Color(0xFF2E7D32)
private val ZSCORE_LINE_WFH = Color(0xFFEF6C00)
private val ZSCORE_LINE_HCA = Color(0xFF6A1B9A)

@Composable
fun GrowthChartSection(
    growthData: List<GrowthModel>,
    gender: String,
    ageInYears: Int,
    modifier: Modifier = Modifier,
) {
    var style by rememberSaveable { mutableStateOf(STYLE_ZSCORE) }

    Column(modifier = modifier.fillMaxWidth()) {
        ChartStyleSwitch(
            style = style,
            onStyleChange = { style = it },
        )

        Spacer(modifier = Modifier.height(Spacing.sm))

        if (style == STYLE_ZSCORE) {
            ZScoreChartGroup(growthData = growthData)
        } else {
            WhoChartGroup(
                growthData = growthData,
                gender = gender,
                ageInYears = ageInYears,
            )
        }
    }
}

@Composable
private fun ChartStyleSwitch(style: String, onStyleChange: (String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(Spacing.lg))
                .background(BrandGreenSoft)
                .padding(3.dp)
        ) {
            StyleOption("Z-Score", style == STYLE_ZSCORE) { onStyleChange(STYLE_ZSCORE) }
            StyleOption("Grafik WHO", style == STYLE_WHO) { onStyleChange(STYLE_WHO) }
        }
    }
}

@Composable
private fun StyleOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.W500,
        color = if (selected) Color.White else TextSecondary,
        modifier = Modifier
            .clip(RoundedCornerShape(Spacing.lg))
            .background(if (selected) BrandGreen else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    )
}

@Composable
private fun ChartHeading(text: String) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.W500,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = Spacing.xs)
    )
}

@Composable
private fun ZScoreChartGroup(growthData: List<GrowthModel>) {
    ChartHeading("Grafik Z-Score Hasil Perhitungan Standar WHO")

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        ExportableChart(fileName = "zscore-bb-per-umur") {
            ZScoreChart(
                landscape = true,
                title = "BB/U - Berat Badan menurut Umur",
                data = growthData.mapNotNull { entry -> entry.wfa_zscore?.let { Pair(entry.age, it) } },
                lineColor = ZSCORE_LINE_WFA,
            )
        }
        ExportableChart(fileName = "zscore-tb-per-umur") {
            ZScoreChart(
                landscape = true,
                title = "TB/U - Tinggi Badan menurut Umur",
                data = growthData.mapNotNull { entry -> entry.hfa_zscore?.let { Pair(entry.age, it) } },
                lineColor = ZSCORE_LINE_HFA,
            )
        }
        ExportableChart(fileName = "zscore-bb-per-tb") {
            ZScoreChart(
                landscape = true,
                title = "BB/TB - Berat Badan menurut Tinggi Badan",
                data = growthData.mapNotNull { entry -> entry.wfh_zscore?.let { Pair(entry.age, it) } },
                lineColor = ZSCORE_LINE_WFH,
            )
        }
        ExportableChart(fileName = "zscore-lingkar-kepala") {
            ZScoreChart(
                landscape = true,
                title = "LK/U - Lingkar Kepala menurut Umur",
                data = growthData.mapNotNull { entry -> entry.head_circum_zscore?.let { Pair(entry.age, it) } },
                lineColor = ZSCORE_LINE_HCA,
            )
        }

        growthData.maxByOrNull { entry -> entry.age }?.let { latest ->
            Spacer(modifier = Modifier.height(12.dp))
            AssessmentCard(growth = latest)
        }
    }
}

@Composable
private fun WhoChartGroup(
    growthData: List<GrowthModel>,
    gender: String,
    ageInYears: Int,
) {
    val heightByAge = growthData.map { entry -> Pair(entry.age, entry.height.toInt()) }
    val weightByAge = growthData.map { entry -> Pair(entry.age, entry.weight.toFloat()) }
    val weightByHeight = growthData.map { entry -> Pair(entry.height.toInt(), entry.weight.toFloat()) }

    ChartHeading("Berdasarkan Panjang Badan atau Tinggi Badan Menurut Umur (PB/U atau TB/U)")
    ExportableChart(fileName = "who-tb-per-umur-0-2") {
        GrowthChart02(data = heightByAge.filter { it.first in 0..23 }, gender = gender)
    }
    if (ageInYears >= 2) {
        ExportableChart(fileName = "who-tb-per-umur-2-5") {
            GrowthChart25(data = heightByAge.filter { it.first in 24..59 }, gender = gender)
        }
    }

    ChartDivider()

    ChartHeading("Berdasarkan Berat Badan Menurut Umur (BB/U)")
    ExportableChart(fileName = "who-bb-per-umur-0-2") {
        WfaChart02(data = weightByAge.filter { it.first in 0..23 }, gender = gender)
    }
    if (ageInYears >= 2) {
        ExportableChart(fileName = "who-bb-per-umur-2-5") {
            WfaChart25(data = weightByAge.filter { it.first in 24..59 }, gender = gender)
        }
    }

    ChartDivider()

    ChartHeading("Berdasarkan Berat Badan Menurut Tinggi Badan (BB/TB)")
    ExportableChart(fileName = "who-bb-per-tb") {
        WflhChart(data = weightByHeight, gender = gender)
    }
}

@Composable
private fun ChartDivider() {
    Spacer(modifier = Modifier.height(Spacing.sm))
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
        thickness = 1.dp
    )
    Spacer(modifier = Modifier.height(Spacing.sm))
}
