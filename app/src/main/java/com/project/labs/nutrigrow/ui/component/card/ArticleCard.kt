package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.util.Locale

@Composable
fun ArticleCard (
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
            AsyncImage(
                model = image,
                contentDescription = "Article Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
            Column(
                modifier = Modifier.padding( start = 15.dp, end = 15.dp, bottom = 15.dp)
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
                    maxLines = 3,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}