package com.project.labs.nutrigrow.ui.screen.validation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.project.labs.nutrigrow.data.model.IndicatorResult
import com.project.labs.nutrigrow.ui.state.StatusBerat
import com.project.labs.nutrigrow.ui.state.StatusNormal
import com.project.labs.nutrigrow.ui.state.StatusRingan
import com.project.labs.nutrigrow.ui.state.StatusSedang
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.TextMuted
import com.project.labs.nutrigrow.ui.theme.TextSecondary

fun bandColor(z: Double?): Color = when {
    z == null -> TextMuted
    z < -3.0 || z > 3.0 -> StatusBerat
    z < -2.0 || z > 2.0 -> StatusSedang
    z > 1.0 -> StatusRingan
    else -> StatusNormal
}

fun formatZ(z: Double?): String =
    if (z == null) "-" else String.format("%+.2f", z).replace('.', ',')

fun formatNumber(value: Double): String =
    (if (value % 1.0 == 0.0) value.toInt().toString() else String.format("%.1f", value))
        .replace('.', ',')

@Composable
fun ReferenceTable(indicator: IndicatorResult) {
    val reference = indicator.reference

    if (reference == null) {
        Text(
            text = "Tabel WHO untuk ukuran ini tidak tersedia.",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
            modifier = Modifier.padding(top = Spacing.xs),
        )
        return
    }

    Column(modifier = Modifier.padding(top = Spacing.xs)) {
        Text(
            text = "Titik SD WHO pada ${reference.basis}",
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Row(modifier = Modifier.fillMaxWidth()) {
            reference.points().forEach { (label, value) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                    )
                    Text(
                        text = formatNumber(value),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Nilai balita: ${formatNumber(indicator.measured)} ${indicator.unit}",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
        )
    }
}
