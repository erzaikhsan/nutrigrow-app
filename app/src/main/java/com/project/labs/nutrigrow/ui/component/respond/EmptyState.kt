package com.project.labs.nutrigrow.ui.component.respond

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.TextMuted
import com.project.labs.nutrigrow.ui.theme.TextPrimary

@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = TextPrimary,
        )
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = TextMuted,
                modifier = Modifier.padding(top = Spacing.xs),
            )
        }
        if (action != null) {
            Column(modifier = Modifier.padding(top = Spacing.md)) {
                action()
            }
        }
    }
}
