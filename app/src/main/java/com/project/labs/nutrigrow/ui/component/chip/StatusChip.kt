package com.project.labs.nutrigrow.ui.component.chip

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.labs.nutrigrow.ui.theme.Radius

@Composable
fun StatusChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = readableOn(color),
        modifier = modifier
            .clip(RoundedCornerShape(Radius.pill))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}

fun readableOn(background: Color): Color =
    if (background.luminance() > 0.5f) Color(0xFF1B1B1B) else Color.White
