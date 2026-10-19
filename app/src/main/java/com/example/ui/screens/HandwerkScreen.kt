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
import com.example.data.CraftType
import com.example.data.PlaceWithMaterials
import com.example.data.SiteType
import com.example.ui.components.PlaceCard
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun HandwerkScreen(
    places: List<PlaceWithMaterials>,
    userLocation: Pair<Double, Double>?,
    selectedCraftFilter: CraftType?,
    onCraftFilterChange: (CraftType?) -> Unit,
    onPlaceClick: (PlaceWithMaterials) -> Unit,
    modifier: Modifier = Modifier
) {
    var siteTypeToggle by remember { mutableStateOf<SiteType?>(null) } // null = Alle, FIRMENSITZ, LAGERORT

    val filtered = places.filter { item ->
        val p = item.place
        val craft = runCatching { CraftType.valueOf(p.craftType) }.getOrDefault(CraftType.OTHER)
        val site = runCatching { SiteType.valueOf(p.siteType) }.getOrDefault(SiteType.SONSTIGES)

        val matchesCraft = if (selectedCraftFilter != null) {
            craft == selectedCraftFilter
        } else {
            craft in listOf(
                CraftType.ROOFER,
                CraftType.PLUMBER,
                CraftType.ELECTRICIAN,
                CraftType.CABLE_WORKS,
                CraftType.BRASS_WORKSHOP
            )
        }

        val matchesSite = if (siteTypeToggle != null) site == siteTypeToggle else true
        matchesCraft && matchesSite
    }

    val totalWeight = filtered.sumOf { it.totalWeightKg }
    val totalValue = filtered.sumOf { it.totalEstimatedValueEur }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Trade Header Dashboard
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
                        text = "HANDWERKSBETRIEBE & LAGERORTE",
                        color = CopperOrange,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${filtered.size} Standorte",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Identifikation von Firmensitzen und Lagerorten für Dachdecker, Installateure, Elektriker, Kabelwerke & Messingwerkstätten.",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Metallgewicht: %.1f kg".format(totalWeight), color = TextSecondary, fontSize = 11.sp)
                    Text("Geschätzter Wert: %,.2f €".format(totalValue), color = GoldPrecious, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Craft Category Filter Chips (Horizontal Scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedCraftFilter == null,
                onClick = { onCraftFilterChange(null) },
                label = { Text("Alle Handwerke", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedCraftFilter == CraftType.CABLE_WORKS,
                onClick = { onCraftFilterChange(CraftType.CABLE_WORKS) },
                label = { Text("Kabelwerke", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CopperOrange.copy(alpha = 0.2f),
                    selectedLabelColor = CopperOrange,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedCraftFilter == CraftType.BRASS_WORKSHOP,
                onClick = { onCraftFilterChange(CraftType.BRASS_WORKSHOP) },
                label = { Text("Messing & CU", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrassYellow.copy(alpha = 0.2f),
                    selectedLabelColor = BrassYellow,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedCraftFilter == CraftType.ROOFER,
                onClick = { onCraftFilterChange(CraftType.ROOFER) },
                label = { Text("Dachdecker", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CopperOrange.copy(alpha = 0.2f),
                    selectedLabelColor = CopperOrange,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedCraftFilter == CraftType.PLUMBER,
                onClick = { onCraftFilterChange(CraftType.PLUMBER) },
                label = { Text("Gas & Wasser", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = selectedCraftFilter == CraftType.ELECTRICIAN,
                onClick = { onCraftFilterChange(CraftType.ELECTRICIAN) },
                label = { Text("Elektriker", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = GoldPrecious.copy(alpha = 0.2f),
                    selectedLabelColor = GoldPrecious,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )
        }

        // Site Type Secondary Chips: Firmensitz vs Lagerort
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = siteTypeToggle == null,
                onClick = { siteTypeToggle = null },
                label = { Text("Sitze & Lager", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = siteTypeToggle == SiteType.FIRMENSITZ,
                onClick = { siteTypeToggle = SiteType.FIRMENSITZ },
                label = { Text("Nur Firmensitze", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = siteTypeToggle == SiteType.LAGERORT,
                onClick = { siteTypeToggle = SiteType.LAGERORT },
                label = { Text("Nur Lagerorte", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrassYellow.copy(alpha = 0.2f),
                    selectedLabelColor = BrassYellow,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )
        }

        // List of Places
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
                            text = "Keine Handwerksbetriebe für diese Filterung vorhanden.\nNutze den Radar-Scan oder lege einen Standort an.",
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
