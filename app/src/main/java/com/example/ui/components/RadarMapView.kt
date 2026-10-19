package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlaceWithMaterials
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.GoldPrecious
import kotlin.math.cos

@Composable
fun RadarMapView(
    places: List<PlaceWithMaterials>,
    centerLocation: Pair<Double, Double>,
    selectedPlaceId: String?,
    onPlaceClick: (PlaceWithMaterials) -> Unit,
    modifier: Modifier = Modifier
) {
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    zoomScale = (zoomScale * zoom).coerceIn(0.5f, 5.0f)
                    panOffset += pan
                }
            }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(places, zoomScale, panOffset, centerLocation) {
                    detectTapGestures { tapOffset ->
                        val centerX = size.width / 2f + panOffset.x
                        val centerY = size.height / 2f + panOffset.y
                        val baseScale = (size.width / 0.08f) * zoomScale

                        val tapped = places.find { p ->
                            val dLon = (p.place.lon - centerLocation.second) * cos(Math.toRadians(centerLocation.first))
                            val dLat = p.place.lat - centerLocation.first
                            val px = centerX + (dLon * baseScale).toFloat()
                            val py = centerY - (dLat * baseScale).toFloat()

                            val dist = Offset(px - tapOffset.x, py - tapOffset.y).getDistance()
                            dist < 32f
                        }
                        if (tapped != null) {
                            onPlaceClick(tapped)
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f + panOffset.x
            val centerY = height / 2f + panOffset.y

            // Draw tactical grid
            val gridSpacing = 60f * zoomScale
            var gx = centerX % gridSpacing
            while (gx < width) {
                drawLine(
                    color = Color(0xFF131D2A),
                    start = Offset(gx, 0f),
                    end = Offset(gx, height),
                    strokeWidth = 1f
                )
                gx += gridSpacing
            }
            var gy = centerY % gridSpacing
            while (gy < height) {
                drawLine(
                    color = Color(0xFF131D2A),
                    start = Offset(0f, gy),
                    end = Offset(width, gy),
                    strokeWidth = 1f
                )
                gy += gridSpacing
            }

            // Crosshairs
            drawLine(
                color = CyanPrimary.copy(alpha = 0.4f),
                start = Offset(centerX, 0f),
                end = Offset(centerX, height),
                strokeWidth = 1.5f
            )
            drawLine(
                color = CyanPrimary.copy(alpha = 0.4f),
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 1.5f
            )

            // Radar concentric rings (1km, 2km, 3km radius approximations)
            val baseScale = (width / 0.08f) * zoomScale
            val kmInDegrees = 1.0 / 111.0
            val r1 = (kmInDegrees * baseScale).toFloat()
            val r2 = (2.0 * kmInDegrees * baseScale).toFloat()
            val r3 = (3.0 * kmInDegrees * baseScale).toFloat()

            drawCircle(color = CyanPrimary.copy(alpha = 0.25f), radius = r1, center = Offset(centerX, centerY), style = Stroke(1.5f))
            drawCircle(color = CyanPrimary.copy(alpha = 0.18f), radius = r2, center = Offset(centerX, centerY), style = Stroke(1f))
            drawCircle(color = CyanPrimary.copy(alpha = 0.12f), radius = r3, center = Offset(centerX, centerY), style = Stroke(1f))

            // Center User Location Indicator
            drawCircle(color = CyanPrimary, radius = 6f, center = Offset(centerX, centerY))
            drawCircle(color = CyanPrimary.copy(alpha = 0.35f), radius = 14f, center = Offset(centerX, centerY), style = Stroke(2f))

            // Draw Place Markers
            for (item in places) {
                val p = item.place
                val dLon = (p.lon - centerLocation.second) * cos(Math.toRadians(centerLocation.first))
                val dLat = p.lat - centerLocation.first

                val px = centerX + (dLon * baseScale).toFloat()
                val py = centerY - (dLat * baseScale).toFloat()

                // Check visibility
                if (px < -50 || px > width + 50 || py < -50 || py > height + 50) continue

                val isSelected = p.id == selectedPlaceId
                val markerColor = when {
                    item.hasHighValueOrPreciousMetals -> GoldPrecious
                    p.category.contains("Kabel") -> CopperOrange
                    p.category.contains("Messing") -> BrassYellow
                    p.siteType.contains("AKTIV") -> EmeraldActive
                    p.siteType.contains("PASSIV") -> CopperOrange
                    else -> CyanPrimary
                }

                val markerSize = if (isSelected) 18f else 12f

                when (p.markerShape) {
                    "square" -> {
                        drawRect(
                            color = markerColor,
                            topLeft = Offset(px - markerSize / 2, py - markerSize / 2),
                            size = androidx.compose.ui.geometry.Size(markerSize, markerSize)
                        )
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(px - markerSize / 2, py - markerSize / 2),
                            size = androidx.compose.ui.geometry.Size(markerSize, markerSize),
                            style = Stroke(1.5f)
                        )
                    }
                    "triangle" -> {
                        val path = Path().apply {
                            moveTo(px, py - markerSize / 1.5f)
                            lineTo(px + markerSize / 1.5f, py + markerSize / 1.5f)
                            lineTo(px - markerSize / 1.5f, py + markerSize / 1.5f)
                            close()
                        }
                        drawPath(path, color = markerColor)
                        drawPath(path, color = Color.Black, style = Stroke(1.5f))
                    }
                    else -> {
                        drawCircle(color = markerColor, radius = markerSize / 2, center = Offset(px, py))
                        drawCircle(color = Color.Black, radius = markerSize / 2, center = Offset(px, py), style = Stroke(1.5f))
                    }
                }

                // If selected, draw glowing ring
                if (isSelected) {
                    drawCircle(
                        color = Color.White,
                        radius = markerSize + 4f,
                        center = Offset(px, py),
                        style = Stroke(2f)
                    )
                }
            }
        }

        // Radar Overlay Labels
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "RADAR-MODUS · ZOOM: %.1fx · %d ORTE".format(zoomScale, places.size),
                color = CyanPrimary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 44.dp)
                .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "● 1-3 km Ringe | Tippen zum Auswählen",
                color = Color.White,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
