package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.ui.component.chip.StatusChip
import com.project.labs.nutrigrow.ui.state.formatZScore
import com.project.labs.nutrigrow.ui.state.zScoreColor
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.Radius
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.SurfaceCard

@Composable
fun MeasurementCard(
    label: String,
    value: String,
    statusLabel: String,
    statusColor: Color,
    modifier: Modifier = Modifier,
    zScore: Double? = null,
    showZScore: Boolean = true,
    advice: String? = null,
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(SurfaceCard),
        shape = RoundedCornerShape(Radius.lg),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandGreen)
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }

        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                StatusChip(label = statusLabel, color = statusColor)
                if (showZScore) {
                    Text(
                        text = formatZScore(zScore),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = zScoreColor(zScore),
                    )
                }
            }

            if (!advice.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(Spacing.sm))
                ExpandableInfoCard(title = "Interpretasi :", body = advice)
            }
        }
    }
}
