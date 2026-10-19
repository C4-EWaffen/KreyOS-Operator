package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.CraftType
import com.example.data.PlaceWithMaterials
import com.example.data.SiteType
import com.example.ui.components.PlaceCard
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GewerbeScreen(
    places: List<PlaceWithMaterials>,
    userLocation: Pair<Double, Double>?,
    onPlaceClick: (PlaceWithMaterials) -> Unit,
    modifier: Modifier = Modifier
) {
    var subFilter by remember { mutableStateOf("ALL") }

    val commercialPlaces = places.filter { item ->
        val p = item.place
        val site = runCatching { SiteType.valueOf(p.siteType) }.getOrDefault(SiteType.SONSTIGES)
        val craft = runCatching { CraftType.valueOf(p.craftType) }.getOrDefault(CraftType.OTHER)

        when (subFilter) {
            "FABRIK" -> site == SiteType.FABRIKHALLEN || craft == CraftType.FACTORY_HALL || craft == CraftType.CABLE_WORKS
            "WERKSTATT" -> site == SiteType.WERKSTATT || craft == CraftType.BRASS_WORKSHOP
            "LAGER" -> site == SiteType.LAGERORT
            else -> true
        }
    }

    val totalWeight = commercialPlaces.sumOf { it.totalWeightKg }
    val totalValue = commercialPlaces.sumOf { it.totalEstimatedValueEur }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Commercial Exclusion & Scope Notice
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(6.dp))
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEWERBEIMMOBILIEN-RADAR",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${commercialPlaces.size} Objekte",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Ausschließlich Industrie, Fabriken, Hallen & Werkstätten. Wohnimmobilien & Gastronomie sind strikt ausgeschlossen.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Metall-Volumen: %.1f kg".format(totalWeight), color = TextSecondary, fontSize = 11.sp)
                    Text("Gesamtwert: %,.2f €".format(totalValue), color = GoldPrecious, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Subfilter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = subFilter == "ALL",
                onClick = { subFilter = "ALL" },
                label = { Text("Alle (${places.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = subFilter == "FABRIK",
                onClick = { subFilter = "FABRIK" },
                label = { Text("Fabrikhallen", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = subFilter == "WERKSTATT",
                onClick = { subFilter = "WERKSTATT" },
                label = { Text("Werkstätten", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = subFilter == "LAGER",
                onClick = { subFilter = "LAGER" },
                label = { Text("Lagerhallen", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )
        }

        // Places List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (commercialPlaces.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keine Gewerbeimmobilien für diesen Filter gefunden.\nStarte einen 'RADAR'-Scan oder passe die Filter an.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(commercialPlaces) { placeItem ->
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
