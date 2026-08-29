package com.project.labs.nutrigrow.ui.component.graph.zscore

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.project.labs.nutrigrow.ui.state.StatusBerat
import com.project.labs.nutrigrow.ui.state.StatusNormal
import com.project.labs.nutrigrow.ui.state.StatusSedang
import kotlin.math.round

private const val Z_MIN = -4f
private const val Z_MAX = 4f
private const val AGE_MAX_DEFAULT = 60

private val PORTRAIT_HEIGHT = 220.dp
private val LANDSCAPE_SPAN = 520.dp

private const val PORTRAIT_LEGEND = "Sumbu datar: umur (bulan) · Sumbu tegak: z-score (SD)"
private const val LANDSCAPE_LEGEND = "Umur (bulan) dibaca ke bawah · z-score (SD) dibaca ke kanan"

private val VALUE_LABEL_STYLE = TextStyle(fontSize = 9.sp, color = Color(0xFF37474F))

@Composable
fun ZScoreChart(
    title: String,
    data: List<Pair<Int, Double>>,
    lineColor: Color,
    modifier: Modifier = Modifier,
    landscape: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp),
        )

        if (data.isEmpty()) {
            Text(
                text = "Belum ada data penimbangan yang bisa dihitung",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575),
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            )
            return@Column
        }

        if (landscape) {
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LANDSCAPE_SPAN),
            ) {
                ZScoreCanvas(
                    data = data,
                    lineColor = lineColor,
                    modifier = Modifier
                        .requiredWidth(LANDSCAPE_SPAN)
                        .requiredHeight(maxWidth)
                        .graphicsLayer { rotationZ = 90f },
                )
            }
        } else {
            ZScoreCanvas(
                data = data,
                lineColor = lineColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PORTRAIT_HEIGHT),
            )
        }

        Text(
            text = if (landscape) LANDSCAPE_LEGEND else PORTRAIT_LEGEND,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF757575),
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun ZScoreCanvas(
    data: List<Pair<Int, Double>>,
    lineColor: Color,
    modifier: Modifier = Modifier,
) {
    val measurer = rememberTextMeasurer()

    Canvas(modifier = modifier.padding(start = 8.dp, end = 12.dp, bottom = 8.dp)) {
        val left = 38.dp.toPx()
        val bottom = size.height - 22.dp.toPx()
        val top = 8.dp.toPx()
        val right = size.width

        val plotWidth = right - left
        val plotHeight = bottom - top

        val ageMax = maxOf(data.maxOf { it.first }, 12).coerceAtMost(AGE_MAX_DEFAULT)
        val ageSpan = if (ageMax <= 0) 1f else ageMax.toFloat()

        fun xOf(age: Int): Float = left + (age.coerceIn(0, ageMax) / ageSpan) * plotWidth
        fun yOf(z: Float): Float =
            top + ((Z_MAX - z.coerceIn(Z_MIN, Z_MAX)) / (Z_MAX - Z_MIN)) * plotHeight

        drawBand(yOf(4f), yOf(3f), StatusBerat.copy(alpha = 0.12f), left, plotWidth)
        drawBand(yOf(3f), yOf(2f), StatusSedang.copy(alpha = 0.15f), left, plotWidth)
        drawBand(yOf(2f), yOf(-2f), StatusNormal.copy(alpha = 0.13f), left, plotWidth)
        drawBand(yOf(-2f), yOf(-3f), StatusSedang.copy(alpha = 0.15f), left, plotWidth)
        drawBand(yOf(-3f), yOf(-4f), StatusBerat.copy(alpha = 0.12f), left, plotWidth)

        for (z in listOf(-3f, -2f, 0f, 2f, 3f)) {
            val y = yOf(z)
            drawLine(
                color = if (z == 0f) Color(0xFF616161) else Color(0xFFBDBDBD),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = if (z == 0f) 1.5.dp.toPx() else 1.dp.toPx(),
            )
            drawLabel(measurer, formatBand(z), Offset(0f, y - 6.dp.toPx()))
        }

        drawLine(
            color = Color(0xFF616161),
            start = Offset(left, top),
            end = Offset(left, bottom),
            strokeWidth = 1.5.dp.toPx(),
        )

        val step = if (ageMax <= 12) 3 else 6
        var age = 0
        while (age <= ageMax) {
            drawLabel(measurer, age.toString(), Offset(xOf(age) - 4.dp.toPx(), bottom + 4.dp.toPx()))
            age += step
        }

        val points = data
            .sortedBy { it.first }
            .map { (ageValue, zValue) -> Offset(xOf(ageValue), yOf(zValue.toFloat())) }

        drawPath(
            path = smoothPath(points, top, bottom),
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
        )

        for (point in points) {
            drawCircle(color = Color.White, center = point, radius = 4.5.dp.toPx())
            drawCircle(color = lineColor, center = point, radius = 3.dp.toPx())
        }

        var lastLabelRight = Float.NEGATIVE_INFINITY
        val labelGap = 4.dp.toPx()
        data.sortedBy { it.first }.forEachIndexed { index, (_, zValue) ->
            val point = points[index]
            val text = formatZScore(zValue)
            val textSize = measurer.measure(text, VALUE_LABEL_STYLE).size

            val x = (point.x - textSize.width / 2f).coerceIn(0f, size.width - textSize.width)
            if (x < lastLabelRight + labelGap) return@forEachIndexed

            val above = point.y - textSize.height - 7.dp.toPx()
            val y = if (above < top) point.y + 7.dp.toPx() else above

            drawText(
                textMeasurer = measurer,
                text = text,
                topLeft = Offset(x, y),
                style = VALUE_LABEL_STYLE,
            )
            lastLabelRight = x + textSize.width
        }
    }
}

private fun DrawScope.smoothPath(points: List<Offset>, top: Float, bottom: Float): Path {
    val path = Path()
    if (points.isEmpty()) return path

    path.moveTo(points.first().x, points.first().y)
    if (points.size == 1) return path

    for (i in 0 until points.size - 1) {
        val previous = points[(i - 1).coerceAtLeast(0)]
        val current = points[i]
        val next = points[i + 1]
        val after = points[(i + 2).coerceAtMost(points.size - 1)]

        path.cubicTo(
            current.x + (next.x - previous.x) / 6f,
            (current.y + (next.y - previous.y) / 6f).coerceIn(top, bottom),
            next.x - (after.x - current.x) / 6f,
            (next.y - (after.y - current.y) / 6f).coerceIn(top, bottom),
            next.x,
            next.y,
        )
    }

    return path
}

private fun formatZScore(z: Double): String {
    val rounded = round(z * 10.0) / 10.0
    return rounded.toString().replace('.', ',')
}

private fun formatBand(z: Float): String = when {
    z > 0 -> "+${z.toInt()}"
    z < 0 -> z.toInt().toString()
    else -> "0"
}

private fun DrawScope.drawBand(yTop: Float, yBottom: Float, color: Color, left: Float, width: Float) {
    drawRect(
        color = color,
        topLeft = Offset(left, yTop),
        size = Size(width, yBottom - yTop),
    )
}

private fun DrawScope.drawLabel(measurer: TextMeasurer, text: String, at: Offset) {
    drawText(
        textMeasurer = measurer,
        text = text,
        topLeft = at,
        style = TextStyle(fontSize = 9.sp, color = Color(0xFF616161)),
    )
}
