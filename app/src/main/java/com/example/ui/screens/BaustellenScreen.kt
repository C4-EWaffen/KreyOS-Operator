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
import com.example.ui.theme.AmberPassive
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun BaustellenScreen(
    places: List<PlaceWithMaterials>,
    userLocation: Pair<Double, Double>?,
    onPlaceClick: (PlaceWithMaterials) -> Unit,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("ALL") } // ALL, AKTIV, PASSIV

    val constructionPlaces = places.filter { item ->
        val p = item.place
        val site = runCatching { SiteType.valueOf(p.siteType) }.getOrDefault(SiteType.SONSTIGES)
        val craft = runCatching { CraftType.valueOf(p.craftType) }.getOrDefault(CraftType.OTHER)

        val isConstruction = craft == CraftType.CONSTRUCTION ||
                site in listOf(SiteType.KUNDEN_BAUSTELLE_AKTIV, SiteType.KUNDEN_BAUSTELLE_PASSIV)

        if (!isConstruction) return@filter false

        when (statusFilter) {
            "AKTIV" -> site == SiteType.KUNDEN_BAUSTELLE_AKTIV
            "PASSIV" -> site == SiteType.KUNDEN_BAUSTELLE_PASSIV
            else -> true
        }
    }

    val totalWeight = constructionPlaces.sumOf { it.totalWeightKg }
    val totalValue = constructionPlaces.sumOf { it.totalEstimatedValueEur }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Construction Sites Dashboard Header
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
                        text = "KUNDEN-BAUSTELLEN MANAGEMENT",
                        color = EmeraldActive,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${constructionPlaces.size} Baustellen",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Überwachung von aktiven Großbaustellen (laufende Montagen) und passiven Objekten (Baupausen / Demontage-Materiallager).",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Gesichtetes Material: %.1f kg".format(totalWeight), color = TextSecondary, fontSize = 11.sp)
                    Text("Geschätzter Wert: %,.2f €".format(totalValue), color = GoldPrecious, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Active vs Passive Toggle Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = statusFilter == "ALL",
                onClick = { statusFilter = "ALL" },
                label = { Text("Alle Baustellen", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                    selectedLabelColor = CyanPrimary,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                )
            )

            FilterChip(
                selected = statusFilter == "AKTIV",
                onClick = { statusFilter = "AKTIV" },
                label = { Text("● Aktiv im Bau", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EmeraldActive.copy(alpha = 0.2f),
                    selectedLabelColor = EmeraldActive,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = statusFilter == "AKTIV",
                    borderColor = SurfaceCardBorder,
                    selectedBorderColor = EmeraldActive,
                    borderWidth = 1.dp
                )
            )

            FilterChip(
                selected = statusFilter == "PASSIV",
                onClick = { statusFilter = "PASSIV" },
                label = { Text("● Passiv / Stillstand", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AmberPassive.copy(alpha = 0.2f),
                    selectedLabelColor = AmberPassive,
                    containerColor = SurfaceCard,
                    labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = statusFilter == "PASSIV",
                    borderColor = SurfaceCardBorder,
                    selectedBorderColor = AmberPassive,
                    borderWidth = 1.dp
                )
            )
        }

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (constructionPlaces.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Keine Kundenbaustellen in dieser Ansicht.\nNutze '+ Standort anlegen' um eine Baustelle zu erfassen.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(constructionPlaces) { placeItem ->
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
