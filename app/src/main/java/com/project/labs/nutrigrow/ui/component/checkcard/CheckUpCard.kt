package com.project.labs.nutrigrow.ui.component.checkcard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

@Composable
fun CheckUpCard (
    gender: String,
    age: Int,
    height: Double,
    hfa_status: String,
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00BF63))
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
        ) {
            Text(
                text = "Umur Anak :",
                fontSize = 17.sp,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "$age bulan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00BF63))
                .padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Jenis Kelamin :",
                fontSize = 17.sp,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = if(gender == "M") "Laki-Laki" else "Perempuan",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF00BF63))
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
        ) {
            Text(
                text = "Tinggi Badan :",
                fontSize = 17.sp,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "$height kg",
                color = Color.White,
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
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Interpretasi :",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W600,
                )
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    colors = if (hfa_status == "Normal" ) {
                        CardDefaults.cardColors(Color(0xFF00BF63))
                    } else if (hfa_status == "Stunted" ) {
                        CardDefaults.cardColors(Color(0xFFFF9800))
                    } else if (hfa_status == "Severely Stunted") {
                        CardDefaults.cardColors(Color(0xFFF44336))
                    } else { CardDefaults.cardColors(Color(0xFF929492)) },
                    modifier = Modifier
                        .clip(shape = CardDefaults.shape)
                        .shadow(
                            elevation = 0.dp,
                            spotColor = Color.Transparent,
                            shape = CardDefaults.shape
                        )
                ){
                    Text(
                        text = hfa_status,
                        fontSize = 15.sp,
                        color = Color.White,
                        modifier = Modifier.padding( vertical = 2.dp, horizontal = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Tinggi Badan anak berada pada status ${hfa_status} berdasarkan umur anak. Periksa segera ke dokter spesialis anak atau puskesmas terdekat untuk pemeriksaan dan penanganan lebih lanjut.",
                textAlign = TextAlign.Justify,
                fontSize = 15.sp,
            )
        }
    }
}