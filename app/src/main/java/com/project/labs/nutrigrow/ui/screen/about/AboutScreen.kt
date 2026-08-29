package com.project.labs.nutrigrow.ui.screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.BuildConfig
import com.project.labs.nutrigrow.ui.component.input.SearchCard
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextMuted
import com.project.labs.nutrigrow.ui.theme.TextSecondary

@Composable
fun AboutScreen(
    role: String,
    onOpenTopic: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by rememberSaveable { mutableStateOf("") }
    val topics = aboutTopics(role = role, query = query)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft)
    ) {
        item { AboutHeader() }
        item {
            SearchCard(
                value = query,
                onValueChange = { query = it },
                placeholder = "Cari topik atau istilah",
            )
        }

        if (topics.isEmpty()) {
            item { EmptyResult(query = query) }
        }

        ABOUT_GROUP_ORDER.forEach { group ->
            val inGroup = topics.filter { it.group == group }
            if (inGroup.isEmpty()) return@forEach

            item(key = "header-$group") { GroupHeader(group) }
            item(key = "group-$group") {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.elevatedCardColors(SurfaceCard),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md)
                ) {
                    Column {
                        inGroup.forEachIndexed { index, topic ->
                            TopicRow(topic = topic, onClick = { onOpenTopic(topic.id) })
                            if (index != inGroup.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = Spacing.md),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                    thickness = 1.dp,
                                )
                            }
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xxl)) }
    }
}

@Composable
private fun AboutHeader() {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(SurfaceCard),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = Spacing.md, end = Spacing.md, top = Spacing.md)
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "NutriGrow",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Pemantauan gizi balita Posyandu Desa Jipang, Banyumas",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Versi ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(
                text = "Halaman ini menjelaskan setiap fitur aplikasi sampai ke rumus dan sumber " +
                    "yang dipakainya. Pilih topik untuk membacanya.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun GroupHeader(group: String) {
    Text(
        text = group.uppercase(),
        style = MaterialTheme.typography.bodySmall,
        fontWeight = FontWeight.SemiBold,
        color = TextSecondary,
        modifier = Modifier.padding(
            start = Spacing.lg,
            end = Spacing.md,
            top = Spacing.md,
            bottom = Spacing.xs,
        ),
    )
}

@Composable
private fun TopicRow(topic: AboutTopic, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(Spacing.md)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = topic.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = topic.summary,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
        Spacer(modifier = Modifier.width(Spacing.sm))
        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = BrandGreen,
        )
    }
}

@Composable
private fun EmptyResult(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.lg, vertical = Spacing.xl)
    ) {
        Text(
            text = "Tidak ada topik yang cocok",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = "Tidak ditemukan topik yang memuat \"$query\". Coba kata kunci lain, " +
                "misalnya stunting, z-score, atau KBM.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}
