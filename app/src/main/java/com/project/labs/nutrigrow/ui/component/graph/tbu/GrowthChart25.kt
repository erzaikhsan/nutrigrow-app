package com.project.labs.nutrigrow.ui.component.graph.tbu

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.project.labs.nutrigrow.R

@Composable
fun GrowthChart25(data: List<Pair<Int, Int>>, gender: String) {
    val minX = 76f
    val maxX = 125f
    val rangeX = maxX - minX
    val minY = 24f
    val maxY = 60f
    val rangeY = maxY - minY

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
    ) {
        AsyncImage(
            model = if (gender == "M") R.drawable.boy_tbu_2_5 else R.drawable.girl_tbu_2_5,
            contentDescription = "Growth Chart",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(525.dp)
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(
                    start = 39.dp,
                    bottom = 25.5.dp,
                    end = 22.dp,
                    top = 38.5.dp,
                )
        ) {
            val width = size.width
            val height = size.height

            val xScale = width / rangeX
            val yScale = height / rangeY

            drawLine(
                color = Color.Black,
                start = Offset(0f, 0f),
                end = Offset(0f, height),
                strokeWidth = 2.dp.toPx()
            )
            drawLine(
                color = Color.Black,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 2.dp.toPx()
            )

            if (data.isNotEmpty()) {
                for (i in data.indices) {
                    val (age, heightVal) = data[i]

                    if (heightVal.toFloat() in minX..maxX) {
                        val x = (heightVal - minX) * xScale
                        val y = (age - minY) * yScale

                        drawCircle(
                            color = Color.Blue,
                            center = Offset(x, y),
                            radius = 2.dp.toPx()
                        )

                        if (i > 0) {
                            val (prevAge, prevHeight) = data[i - 1]

                            if (prevHeight.toFloat() in minX..maxX) {
                                val prevX = (prevHeight - minX) * xScale
                                val prevY = (prevAge - minY) * yScale

                                drawLine(
                                    color = Color.Blue,
                                    start = Offset(prevX, prevY),
                                    end = Offset(x, y),
                                    strokeWidth = 2.dp.toPx()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}