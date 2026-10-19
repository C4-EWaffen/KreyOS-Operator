package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MaterialItemEntity
import com.example.data.MetalType
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MaterialItemRow(
    item: MaterialItemEntity,
    onDeleteClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val metal = runCatching { MetalType.valueOf(item.metalType) }.getOrDefault(MetalType.KUPFER_BERRY)
    val metalColor = when {
        metal.group.contains("Kupfer") -> CopperOrange
        metal.group.contains("Messing") -> BrassYellow
        metal.group.contains("Edelmetall") -> GoldPrecious
        metal.group.contains("Bronze") -> Color(0xFFD97706)
        metal.group.contains("Werkzeuge") -> CyanPrimary
        else -> Color(0xFF94A3B8)
    }

    val isUnderground = item.storageLocation.contains("Erde", ignoreCase = true) ||
            item.storageLocation.contains("Erdtrasse", ignoreCase = true) ||
            item.depthMeters > 0

    val isRail = item.storageLocation.contains("Gleis", ignoreCase = true) ||
            item.storageLocation.contains("Rampe", ignoreCase = true) ||
            item.materialName.contains("Oberleitung", ignoreCase = true)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceHighlight, RoundedCornerShape(8.dp))
            .border(1.dp, if (isUnderground) CopperOrange.copy(alpha = 0.4f) else SurfaceCardBorder, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Badges row: Metal Group, Quantity, Storage Location, Condition
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Metal Group
                Box(
                    modifier = Modifier
                        .background(metalColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .border(0.5.dp, metalColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = metal.group,
                        color = metalColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Quantity badge
                if (item.quantity > 0) {
                    Box(
                        modifier = Modifier
                            .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (item.quantity == 1) "Menge: 1 Stk." else "Menge: ${item.quantity} Stk.",
                            color = CyanPrimary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Storage Location badge
                if (item.storageLocation.isNotBlank()) {
                    val locationBg = when {
                        isUnderground -> Color(0xFF78350F).copy(alpha = 0.35f)
                        isRail -> Color(0xFF0369A1).copy(alpha = 0.35f)
                        else -> Color(0xFF1E293B)
                    }
                    val locationText = when {
                        isUnderground -> CopperOrange
                        isRail -> Color(0xFF38BDF8)
                        else -> TextSecondary
                    }

                    Box(
                        modifier = Modifier
                            .background(locationBg, RoundedCornerShape(4.dp))
                            .border(0.5.dp, locationText.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            if (isUnderground) {
                                Icon(Icons.Default.SouthEast, contentDescription = null, tint = locationText, modifier = Modifier.size(9.dp))
                            } else if (isRail) {
                                Icon(Icons.Default.Train, contentDescription = null, tint = locationText, modifier = Modifier.size(9.dp))
                            }
                            Text(
                                text = item.storageLocation,
                                color = locationText,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Depth & Trench length info
                if (item.depthMeters > 0 || item.estimatedLengthMeters > 0) {
                    val infoText = buildString {
                        if (item.depthMeters > 0) append("Tiefe: %.1fm".format(item.depthMeters))
                        if (item.estimatedLengthMeters > 0) {
                            if (isNotEmpty()) append(" · ")
                            append("Trasse: %.0fm".format(item.estimatedLengthMeters))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF451A03), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = infoText,
                            color = Color(0xFFFDBA74),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Auto Detected badge
                if (item.autoDetected) {
                    Text(
                        text = "⚡ AUTO",
                        color = CyanPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Material Name
            Text(
                text = item.materialName,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Weight & Price per kg
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "%.1f kg".format(item.weightKg),
                    color = CopperOrange,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                if (item.pricePerKg > 0.0) {
                    Text(
                        text = "à %.2f €/kg".format(item.pricePerKg),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (item.condition.isNotBlank()) {
                    Text(
                        text = "(${item.condition})",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            if (item.notes.isNotBlank()) {
                Text(
                    text = item.notes,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }
        }

        // Total Value & Delete Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "%,.2f €".format(item.totalValueEur),
                    color = GoldPrecious,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                if (item.quantity > 1) {
                    Text(
                        text = "Gesamt (${item.quantity} Stk.)",
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }
            }

            if (onDeleteClick != null) {
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Material entfernen",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
