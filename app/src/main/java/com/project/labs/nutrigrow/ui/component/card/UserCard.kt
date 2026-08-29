package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import java.util.Locale

@Composable
fun UserCard (
    full_name: String,
    gender: String,
    phone_number: String,
    address: String,
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
                .padding(horizontal = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                AsyncImage(
                    model = if (gender == "M") R.drawable.man else R.drawable.woman,
                    contentDescription = "Foto profil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(60.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 10.dp),
                ) {
                    Text(
                        text = full_name.uppercase(Locale.getDefault()),
                        maxLines = 2,
                        lineHeight = 20.sp,
                        fontSize = 15.sp,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "No. Hp   :",
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = phone_number,
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = "Alamat  :",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = address,
                            textAlign = TextAlign.End,
                            maxLines = 3,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier
                                .weight(1f)
                        )
                    }
                }
            }
        }
    }
}