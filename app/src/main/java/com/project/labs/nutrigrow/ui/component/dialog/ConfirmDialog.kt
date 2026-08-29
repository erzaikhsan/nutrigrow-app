package com.project.labs.nutrigrow.ui.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.SurfaceCard

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String = "Batal",
    confirmColor: Color = BrandGreen,
    secondaryText: String? = null,
    onSecondary: (() -> Unit)? = null,
    secondaryColor: Color = BrandGreen,
) {
    AlertDialog(
        containerColor = SurfaceCard,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmText, color = confirmColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            if (secondaryText != null && onSecondary != null) {
                TextButton(onClick = onSecondary) {
                    Text(text = secondaryText, color = secondaryColor, fontWeight = FontWeight.SemiBold)
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(text = dismissText)
                }
            }
        },
    )
}
