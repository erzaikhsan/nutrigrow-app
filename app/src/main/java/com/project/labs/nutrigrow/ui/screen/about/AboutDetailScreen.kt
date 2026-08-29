package com.project.labs.nutrigrow.ui.screen.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.ui.theme.BrandGreen
import com.project.labs.nutrigrow.ui.theme.BrandGreenSoft
import com.project.labs.nutrigrow.ui.theme.Spacing
import com.project.labs.nutrigrow.ui.theme.SurfaceCard
import com.project.labs.nutrigrow.ui.theme.TextMuted
import com.project.labs.nutrigrow.ui.theme.TextSecondary

@Composable
fun AboutDetailScreen(
    topic: AboutTopic?,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BrandGreenSoft),
        contentPadding = PaddingValues(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        if (topic == null) {
            item { MissingTopic() }
            return@LazyColumn
        }

        item {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(SurfaceCard),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    Text(
                        text = topic.group.uppercase(),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandGreen,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = topic.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = topic.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
        }

        item {
            ElevatedCard(
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.elevatedCardColors(SurfaceCard),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(Spacing.lg)) {
                    topic.blocks.forEachIndexed { index, block ->
                        if (index > 0) Spacer(modifier = Modifier.height(Spacing.sm))
                        BlockView(block)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(Spacing.xl)) }
    }
}

@Composable
private fun BlockView(block: AboutBlock) {
    when (block) {
        is AboutBlock.Paragraph -> Text(
            text = block.text,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 21.sp,
        )

        is AboutBlock.Heading -> Text(
            text = block.text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = Spacing.xs),
        )

        is AboutBlock.Bullets -> Column {
            block.items.forEach { item ->
                Row(modifier = Modifier.padding(bottom = Spacing.xs)) {
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandGreen,
                    )
                    Spacer(modifier = Modifier.width(Spacing.sm))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp,
                    )
                }
            }
        }

        is AboutBlock.Table -> TableView(block)

        is AboutBlock.Formula -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Spacing.xs))
                .background(BrandGreenSoft)
                .padding(Spacing.md)
        ) {
            Text(
                text = block.lines.joinToString("\n"),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                lineHeight = 19.sp,
            )
        }

        is AboutBlock.Note -> Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(Spacing.xs))
                .background(BrandGreenSoft)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(BrandGreen)
            )
            Text(
                text = block.text,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 21.sp,
                modifier = Modifier.padding(Spacing.md),
            )
        }

        is AboutBlock.Reference -> Column(modifier = Modifier.padding(bottom = Spacing.sm)) {
            Text(
                text = block.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 21.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Dipakai untuk: ${block.usedFor}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp,
            )
        }
    }
}

@Composable
private fun TableView(table: AboutBlock.Table) {
    val weights = columnWeights(table.headers.size)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Spacing.xs))
            .background(BrandGreenSoft)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(Spacing.sm)) {
            table.headers.forEachIndexed { index, header ->
                Text(
                    text = header,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = TextSecondary,
                    modifier = Modifier
                        .weight(weights[index])
                        .padding(horizontal = 4.dp),
                )
            }
        }

        table.rows.forEach { row ->
            HorizontalDivider(
                color = BrandGreen.copy(alpha = 0.25f),
                thickness = 1.dp,
            )
            Row(modifier = Modifier.fillMaxWidth().padding(Spacing.sm)) {
                row.forEachIndexed { index, cell ->
                    Text(
                        text = cell,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        modifier = Modifier
                            .weight(weights.getOrElse(index) { 1f })
                            .padding(horizontal = 4.dp),
                    )
                }
            }
        }
    }
}

private fun columnWeights(count: Int): List<Float> = when (count) {
    2 -> listOf(1f, 1.3f)
    3 -> listOf(1f, 1.6f, 0.9f)
    else -> List(count) { 1f }
}

@Composable
private fun MissingTopic() {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.elevatedCardColors(SurfaceCard),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(Spacing.lg)) {
            Text(
                text = "Topik tidak ditemukan",
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(Spacing.xs))
            Text(
                text = "Kembali ke daftar About NutriGrow, lalu pilih topik dari sana.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
            )
        }
    }
}
