package com.project.labs.nutrigrow.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun EventCard (
    title: String,
    date: String,
    start_time: String,
    end_time: String,
    place: String,
    onClick: () -> Unit,
) {
    fun formatDate(inputDate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
        val outputFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("id", "ID"))
        val zonedDateTime = ZonedDateTime.parse(inputDate, inputFormatter)
        return zonedDateTime.format(outputFormatter)
    }

    fun calculateTimeDifference(date: String, startTime: String): Pair<Long, Long> {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS XXX", Locale.ENGLISH)
        val eventDateTime = ZonedDateTime.parse(date, inputFormatter)

        // Parsing startTime dengan format HH:mm
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()

        // Set jam dan menit untuk eventDateTime
        val eventStartDateTime = eventDateTime.withHour(startHour).withMinute(startMinute)
        val now = ZonedDateTime.now()

        val duration = Duration.between(now, eventStartDateTime)
        val days = duration.toDays()
        val hours = duration.toHours() % 24
        return Pair(days, hours)
    }

    fun isEventFinished(date: String, endTime: String): Boolean {
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss.SSS XXX", Locale.ENGLISH)
        val eventDateTime = ZonedDateTime.parse(date, inputFormatter)

        // Parsing endTime dengan format HH:mm
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        // Set jam dan menit untuk eventDateTime
        val eventEndDateTime = eventDateTime.withHour(endHour).withMinute(endMinute)
        return ZonedDateTime.now().isAfter(eventEndDateTime)
    }

    val (daysLeft, hoursLeft) = calculateTimeDifference(date, start_time)
    val eventFinished = isEventFinished(date, end_time)

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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = R.drawable.posyandu,
                    contentDescription = "Event Image",
                    modifier = Modifier
                        .height(130.dp)
                        .width(120.dp)
                        .clip(MaterialTheme.shapes.medium)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( top = 10.dp, end = 15.dp, bottom = 10.dp),
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tanggal :",
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = formatDate(date),
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Tempat :",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = place,
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Waktu :",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "$start_time - $end_time WIB",
                            maxLines = 1,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = when {
                                eventFinished -> "Selesai"
                                daysLeft > 0 -> "$daysLeft Hari Lagi"
                                hoursLeft > 0 -> "Hari Ini"
                                else -> "Hari Ini"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                        )
                    }
                }
            }
        }
    }
}