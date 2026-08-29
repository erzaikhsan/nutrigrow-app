package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.project.labs.nutrigrow.ui.theme.Radius
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.TextPrimary

@Composable
fun AlertBanner(
    message: String,
    color: Color,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
            .clip(RoundedCornerShape(Radius.md))
            .background(color.copy(alpha = 0.25f))
            .padding(Spacing.sm),
    ) {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
        )
    }
}
