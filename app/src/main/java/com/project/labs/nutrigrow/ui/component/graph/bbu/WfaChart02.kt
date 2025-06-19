package com.project.labs.nutrigrow.ui.component.graph.bbu

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
fun WfaChart02(data: List<Pair<Int, Float>>, gender: String) {
    val minX = 7f
    val maxX = 82f
    val rangeX = maxX - minX
    val maxY = 24f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(550.dp)
    ) {
        AsyncImage(
            model = if (gender == "M") R.drawable.boy_bbu_0_2 else R.drawable.girl_bbu_0_2,
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
                    start = 39.5.dp,
                    bottom = 26.5.dp,
                    end = 22.dp,
                    top = 38.5.dp,
                )
        ) {
            val width = size.width
            val height = size.height

            val xScale = width / rangeX
            val yScale = height / maxY

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
                    val (age, weightVal) = data[i]

                    if ((weightVal * 5) in minX..maxX) {
                        val x = ((weightVal * 5) - minX) * xScale
                        val y = age * yScale

                        drawCircle(
                            color = if ( gender == "M" ) Color.Blue else Color(0xFFFF5EB6),
                            center = Offset(x, y),
                            radius = 2.dp.toPx()
                        )

                        if (i > 0) {
                            val (prevAge, prevWeight) = data[i - 1]

                            if ((prevWeight * 5) in minX..maxX) {
                                val prevX = ((prevWeight * 5) - minX) * xScale
                                val prevY = prevAge * yScale

                                drawLine(
                                    color = if ( gender == "M" ) Color.Blue else Color(0xFFFF5EB6),
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