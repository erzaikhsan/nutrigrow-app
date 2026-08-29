package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.SurfaceCard

@Composable
fun VaccineCard(
    vaccineName: String,
    dateLabel: String,
    place: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(SurfaceCard),
        modifier = modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandGreen)
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Nama Vaksin :",
                fontSize = 17.sp,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = vaccineName,
                color = Color.White,
                textAlign = TextAlign.End,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            VaccineDetailRow(label = "Divaksin pada :", value = dateLabel)
            VaccineDetailRow(label = "Faskes :", value = place)
        }
    }
}

@Composable
private fun VaccineDetailRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.W600,
        )
        Text(
            text = value,
            fontSize = 15.sp,
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 8.dp)
        )
    }
}
