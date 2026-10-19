package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.domain.SunTimes
import com.example.ui.theme.CyanPrimary

@Composable
fun DaylightBar(
    sunTimes: SunTimes,
    modifier: Modifier = Modifier
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF0A1420),
            Color(0xFF1A3A5C),
            Color(0xFFD97706),
            Color(0xFF1A3A5C),
            Color(0xFF0A1420)
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(gradientBrush)
            .border(width = 1.dp, color = CyanPrimary.copy(alpha = 0.35f))
            .padding(horizontal = 14.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = if (sunTimes.isDaylight) "☀" else "☾",
                color = if (sunTimes.isDaylight) Color(0xFFFFCC00) else Color(0xFF94A3B8),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${sunTimes.sunriseStr} – ${sunTimes.sunsetStr}",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = sunTimes.statusText,
            color = Color.White,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
