package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun MpasiCard (
    image: String,
    title: String,
    description: String,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .clickable {
                onClick()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp, top = 5.dp, bottom = 5.dp),
                ) {
                    Text(
                        text = title.uppercase(Locale.getDefault()),
                        maxLines = 2,
                        lineHeight = 20.sp,
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        text = description,
                        maxLines = 5,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                AsyncImage(
                    model = image,
                    contentDescription = "Mpasi Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(7.dp)
                        .size(125.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
            }
        }
    }
}