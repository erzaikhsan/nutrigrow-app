package com.project.labs.nutrigrow.ui.component.snackbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CustomSnackBar(
    @DrawableRes drawableRes: Int,
    message: String,
    containerColor: Color = Color.Black
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 7.dp
        ),
        colors = CardDefaults.cardColors(containerColor),
        modifier = Modifier
            .fillMaxWidth()
            .clip(CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painterResource(id = drawableRes),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}