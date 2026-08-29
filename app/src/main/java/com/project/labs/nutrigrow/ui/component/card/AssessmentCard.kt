package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.data.model.GrowthModel
import com.project.labs.nutrigrow.ui.state.StatusBerat
import com.project.labs.nutrigrow.ui.state.StatusRingan
import com.project.labs.nutrigrow.ui.state.formatZScore
import com.project.labs.nutrigrow.ui.state.gainColor
import com.project.labs.nutrigrow.ui.state.gainLabel
import com.project.labs.nutrigrow.ui.state.hcaColor
import com.project.labs.nutrigrow.ui.state.hfaColor
import com.project.labs.nutrigrow.ui.state.hfaLabel
import com.project.labs.nutrigrow.ui.state.muacColor
import com.project.labs.nutrigrow.ui.state.wfaColor
import com.project.labs.nutrigrow.ui.state.wfaLabel
import com.project.labs.nutrigrow.ui.state.wfhColor
import com.project.labs.nutrigrow.ui.state.wfhLabel
import com.project.labs.nutrigrow.ui.state.zScoreColor

@Composable
fun AssessmentCard(growth: GrowthModel, modifier: Modifier = Modifier) {
    ElevatedCard(
        colors = CardDefaults.cardColors(Color.White),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Hasil Penilaian",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )

            if (growth.needs_referral) {
                Banner(
                    text = "Berat badan tidak naik ${growth.consecutive_no_gain} kali berturut-turut (2T). Rujuk ke tenaga kesehatan.",
                    color = StatusBerat,
                )
            }

            if (growth.is_flagged && growth.flag_reason.isNotBlank()) {
                Banner(text = growth.flag_reason, color = StatusRingan)
            }

            AssessmentRow(
                label = "Berat badan menurut umur",
                value = wfaLabel(growth.wfa_status),
                zScore = formatZScore(growth.wfa_zscore),
                color = wfaColor(growth.wfa_status),
                zColor = zScoreColor(growth.wfa_zscore),
            )
            AssessmentRow(
                label = "Tinggi badan menurut umur",
                value = hfaLabel(growth.hfa_status),
                zScore = formatZScore(growth.hfa_zscore),
                color = hfaColor(growth.hfa_status),
                zColor = zScoreColor(growth.hfa_zscore),
            )
            AssessmentRow(
                label = "Berat badan menurut tinggi",
                value = wfhLabel(growth.wfh_status),
                zScore = formatZScore(growth.wfh_zscore),
                color = wfhColor(growth.wfh_status),
                zColor = zScoreColor(growth.wfh_zscore),
            )
            AssessmentRow(
                label = "Lingkar kepala menurut umur",
                value = growth.head_circum_status,
                zScore = formatZScore(growth.head_circum_zscore),
                color = hcaColor(growth.head_circum_status),
                zColor = zScoreColor(growth.head_circum_zscore),
            )
            AssessmentRow(
                label = "Lingkar lengan atas",
                value = growth.muac_status,
                zScore = "",
                color = muacColor(growth.muac_status),
                zColor = Color.Transparent,
            )
            AssessmentRow(
                label = "Kenaikan berat badan",
                value = gainLabel(growth.gain_status),
                zScore = growth.weight_gain?.let { String.format("%+.2f kg", it) } ?: "",
                color = gainColor(growth.gain_status),
                zColor = gainColor(growth.gain_status),
            )
        }
    }
}

@Composable
private fun Banner(text: String, color: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.25f))
            .padding(10.dp),
    )
}

@Composable
private fun AssessmentRow(
    label: String,
    value: String,
    zScore: String,
    color: Color,
    zColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
            if (zScore.isNotBlank()) {
                Text(
                    text = zScore,
                    style = MaterialTheme.typography.labelSmall,
                    color = zColor,
                )
            }
        }
    }
}
