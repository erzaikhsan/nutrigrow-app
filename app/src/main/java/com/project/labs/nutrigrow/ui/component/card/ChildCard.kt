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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import java.time.*
import java.time.format.DateTimeFormatter

@Composable
fun ChildCard (
    full_name: String,
    gender: String,
    date_of_birth: String,
    color: String,
    onClick: () -> Unit,
) {
    fun calculateAge(dateOfBirth: String): String {
        val dob = LocalDate.parse(dateOfBirth.substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))

        val currentDate = LocalDate.now(ZoneId.systemDefault())

        val period = Period.between(dob, currentDate)

        return "${period.years} tahun ${period.months} bulan"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = CardDefaults.shape)
            .shadow(
                elevation = 0.dp,
                spotColor = Color.Transparent,
                shape = CardDefaults.shape
            )
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .background(if (color == "White") Color.White else Color(0xFFE0FFD2))
                .padding(start = 10.dp, end = 10.dp, bottom = 7.dp, top = 7.dp)
        ) {
            AsyncImage(
                model = if (gender == "M") R.drawable.boy else R.drawable.girl,
                contentDescription = "Foto balita",
                modifier = Modifier
                    .padding(4.dp)
                    .size(45.dp)
                    .width(45.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = full_name,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = calculateAge(date_of_birth),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "",
            )
        }
    }
}