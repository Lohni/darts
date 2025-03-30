package com.lohni.darts.ui.composable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils
import com.lohni.darts.ui.theme.DartsTheme
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun BasicLineChart(
    title: String = "",
    data: Map<String, List<Float>>,
    xAxisLabel: List<String>
) {

    val lineColorList = generateColorVariations(MaterialTheme.colorScheme.primary)
    val legendColor: Color = MaterialTheme.colorScheme.onSurface
    val axisLineColor: Color = MaterialTheme.colorScheme.onSurface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(title, fontSize = 16.sp, modifier = Modifier.padding(start = 4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                val maxValue = data.values.flatten().maxOrNull() ?: 0f
                val minValue = data.values.flatten().minOrNull() ?: 0f
                val labelFontSize = 12.sp

                val textMeasurer = rememberTextMeasurer()

                val labelOffset = textMeasurer.measure(
                    maxValue.roundToInt().toString(),
                    TextStyle(fontSize = labelFontSize)
                )
                val heightOffset = labelOffset.size.height * 1.5f
                val widthOffset = labelOffset.size.width * 1.5f

                Canvas(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val canvasWidth = size.width - widthOffset
                    val canvasHeight = size.height - heightOffset
                    val range = maxValue - minValue
                    val axisLabelCount =
                        max(min(maxValue / max(minValue, 1f), 10f), 4f).roundToInt()
                    val points = mutableListOf<Offset>()

                    data.values.forEachIndexed { idx, series ->
                        val pointCount = series.size
                        val spacing = canvasWidth / (pointCount - 1)

                        val lineColor = lineColorList[idx]
                        val path = Path()

                        series.forEachIndexed { index, value ->
                            val x = index * spacing
                            val y = if (range == 0f) canvasHeight / 2
                            else canvasHeight - ((value - minValue) / range) * canvasHeight

                            points.add(Offset(x, y))
                        }

                        points.forEachIndexed { index, offset ->
                            if (index == 0) {
                                path.moveTo(offset.x + widthOffset, offset.y)
                            } else {
                                path.lineTo(offset.x + widthOffset, offset.y)
                            }
                        }

                        val fillPath = Path().apply {
                            addPath(path)
                            lineTo(canvasWidth + widthOffset, canvasHeight)
                            lineTo(widthOffset, canvasHeight)
                            close()
                        }

                        drawPath(path = fillPath, color = lineColor, alpha = 0.2f)
                        drawPath(
                            path = path,
                            color = lineColor,
                            style = Stroke(width = 3.dp.toPx())
                        )
                        points.clear()
                    }

                    // Draw Y-axis labels and orientation lines
                    for (i in 0 until axisLabelCount) {
                        val labelValue =
                            minValue + (range * (axisLabelCount - 1 - i).toFloat() / (axisLabelCount - 1))
                        val labelY = (canvasHeight / (axisLabelCount - 1)) * i
                        val label = labelValue.roundToInt().toString()

                        // Draw orientation line
                        val textY = if (i > 0) labelY - labelOffset.size.height / 2 else 0f
                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = Offset(0f, textY),
                            style = TextStyle(color = legendColor, fontSize = labelFontSize)
                        )

                        if (i < axisLabelCount - 1) {
                            drawLine(
                                color = axisLineColor,
                                alpha = 0.1f,
                                start = Offset(widthOffset, min(labelY, canvasHeight)),
                                end = Offset(canvasWidth + widthOffset, min(labelY, canvasHeight)),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }

                    // Draw X-axis labels
                    val labelTextStyle = TextStyle(color = legendColor, fontSize = labelFontSize)
                    val yAxisLabelSpacing = canvasWidth / (xAxisLabel.size - 1)
                    xAxisLabel.forEachIndexed { index, label ->
                        val measure = textMeasurer.measure(label, labelTextStyle)
                        val labelX = when (index) {
                            0 -> 0f
                            xAxisLabel.size - 1 -> canvasWidth - measure.size.width
                            else -> (index * yAxisLabelSpacing - (measure.size.width / 2))
                        }

                        drawText(
                            textMeasurer = textMeasurer,
                            text = label,
                            topLeft = Offset(
                                widthOffset + labelX,
                                canvasHeight + heightOffset - labelFontSize.toPx()
                            ),
                            style = labelTextStyle
                        )

                        if (index > 0 && index < xAxisLabel.size - 1) {
                            drawLine(
                                color = axisLineColor,
                                alpha = 0.1f,
                                start = Offset(widthOffset + labelX + measure.size.width / 2, 0f),
                                end = Offset(
                                    widthOffset + labelX + measure.size.width / 2,
                                    canvasHeight + labelFontSize.toPx()
                                ),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                data.keys.forEachIndexed { idx, title ->
                    val lineColor = lineColorList[idx]
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(5.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(lineColor)
                        )
                        Text(
                            text = title,
                            style = TextStyle(
                                color = legendColor,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

fun generateColorVariations(baseColor: Color): List<Color> {
    val variations = mutableListOf<Color>()
    val baseColorHsl = FloatArray(3)
    ColorUtils.RGBToHSL(
        (baseColor.red * 255).roundToInt(),
        (baseColor.green * 255).roundToInt(),
        (baseColor.blue * 255).roundToInt(),
        baseColorHsl
    )

    val baseHue = baseColorHsl[0]
    val baseSaturation = baseColorHsl[1]
    val baseLightness = baseColorHsl[2]

    // Define color theory relationships
    val colorRelationships = listOf(
        0f, // Base color
        180f, // Complementary
        30f, // Analogous 1
        -30f, // Analogous 2
        120f // Triadic
    )

    colorRelationships.forEach { hueOffset ->
        val newHue = (baseHue + hueOffset).coerceIn(0f, 360f)
        val newColor = Color.hsl(
            newHue,
            baseSaturation.coerceIn(0.65f, 1f),
            baseLightness.coerceIn(0.45f, 0.65f)
        )
        variations.add(newColor)
    }

    return variations
}

@Preview
@Composable
fun LineChartPreview() {
    DartsTheme(darkTheme = false) {
        val data = mapOf(
            "Avg1" to listOf(40f, 30f, 38f, 50f, 90f, 21f, 10f),
            "Avg2" to listOf(50f, 30f, 58f, 90f),
            "Avg3" to listOf(80f, 20f, 18f, 20f, 30f),
        )
        BasicLineChart("Test Chart", data = data, listOf())
    }
}