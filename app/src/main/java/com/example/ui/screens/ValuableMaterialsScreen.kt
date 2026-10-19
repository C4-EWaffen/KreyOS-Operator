package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MetalType
import com.example.data.PlaceWithMaterials
import com.example.ui.components.PlaceCard
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ValuableMaterialsScreen(
    places: List<PlaceWithMaterials>,
    userLocation: Pair<Double, Double>?,
    onPlaceClick: (PlaceWithMaterials) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetalCategory by remember { mutableStateOf("ALL") }

    val filtered = places.filter { item ->
        when (selectedMetalCategory) {
            "KUPFER" -> item.materials.any { it.metalType.contains("KUPFER") }
            "MESSING" -> item.materials.any { it.metalType.contains("MESSING") }
            "BRONZE" -> item.materials.any { it.metalType.contains("ROTGUSS") || it.metalType.contains("BRONZE") }
            "EDELMETALL" -> item.materials.any { it.metalType.contains("SILBER") || it.metalType.contains("LOETZINN") || it.metalType.contains("HARTMETALL") }
            else -> item.hasHighValueOrPreciousMetals || item.totalEstimatedValueEur > 0.0
        }
    }.sortedByDescending { it.totalEstimatedValueEur }

    val totalWeight = filtered.sumOf { it.totalWeightKg }
    val totalValue = filtered.sumOf { it.totalEstimatedValueEur }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // High Value Metals Market Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(6.dp))
                .border(1.dp, GoldPrecious.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "WERTSTOFF- & EDELMETALL-RADAR",
                        color = GoldPrecious,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "%,.2f € Gesamtwert".format(totalValue),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Metal benchmark tickers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BenchmarkPill("Cu Millberry", "8.90 €/kg", CopperOrange)
                    BenchmarkPill("Cu Berry", "8.20 €/kg", CopperOrange)
                    BenchmarkPill("Messing MS58", "5.25 €/kg", BrassYellow)
                    BenchmarkPill("Rotguss RG5", "6.90 €/kg", Color(0xFFD97706))
                    BenchmarkPill("Silberkontakte", "720.00 €/kg", GoldPrecious)
                    BenchmarkPill("Lötzinn", "26.00 €/kg", CyanPrimary)
                    BenchmarkPill("Hartmetall", "29.50 €/kg", Color(0xFFC084FC))
                }
            }
        }

        // Filter Chips for valuable metals
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedMetalCategory == "ALL",
                onClick = { selectedMetalCategory = "ALL" },
                label = { Text("Alle Wertstoffe", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrecious.copy(alpha = 0.2f),
                    selectedLabelColor = GoldPrecious,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedMetalCategory == "KUPFER",
                onClick = { selectedMetalCategory = "KUPFER" },
                label = { Text("Kupfer (Cu)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CopperOrange.copy(alpha = 0.2f),
                    selectedLabelColor = CopperOrange,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedMetalCategory == "MESSING",
                onClick = { selectedMetalCategory = "MESSING" },
                label = { Text("Messing (Ms)", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrassYellow.copy(alpha = 0.2f),
                    selectedLabelColor = BrassYellow,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedMetalCategory == "BRONZE",
                onClick = { selectedMetalCategory = "BRONZE" },
                label = { Text("Bronze & Rotguss", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CopperOrange.copy(alpha = 0.2f),
                    selectedLabelColor = CopperOrange,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedMetalCategory == "EDELMETALL",
                onClick = { selectedMetalCategory = "EDELMETALL" },
                label = { Text("Silber & Hartmetall", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrecious.copy(alpha = 0.2f),
                    selectedLabelColor = GoldPrecious,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )
        }

        // List of Ranked Locations by Metal Value
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filtered.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keine Standorte mit diesem Metall-Fokus erfasst.\nNutze 'Auto-Scout' im Objekt-Detail oder lege Standorte an.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(filtered) { placeItem ->
                    PlaceCard(
                        placeWithMaterials = placeItem,
                        userLocation = userLocation,
                        onClick = { onPlaceClick(placeItem) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BenchmarkPill(name: String, price: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
            .border(0.5.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Column {
            Text(name, color = TextMuted, fontSize = 9.sp)
            Text(price, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }
    }
}
