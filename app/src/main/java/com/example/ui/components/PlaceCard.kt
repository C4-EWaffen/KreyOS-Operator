package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CraftType
import com.example.data.PlaceWithMaterials
import com.example.data.SiteType
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaceCard(
    placeWithMaterials: PlaceWithMaterials,
    userLocation: Pair<Double, Double>?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val place = placeWithMaterials.place
    val craft = runCatching { CraftType.valueOf(place.craftType) }.getOrDefault(CraftType.OTHER)
    val site = runCatching { SiteType.valueOf(place.siteType) }.getOrDefault(SiteType.SONSTIGES)

    val distanceStr = if (userLocation != null) {
        val distMeters = calculateDistanceMeters(
            lat1 = userLocation.first,
            lon1 = userLocation.second,
            lat2 = place.lat,
            lon2 = place.lon
        )
        if (distMeters < 1000) "${distMeters.toInt()} m" else "%.2f km".format(distMeters / 1000.0)
    } else null

    val isLostPlace = place.vacancyStatus.contains("Lost Place", ignoreCase = true) || craft == CraftType.LOST_PLACE
    val isLeerstand = place.vacancyStatus.contains("Leerstand", ignoreCase = true) || site == SiteType.LEERSTAND_VERKAUF
    val isRail = place.hasRailAccess || craft == CraftType.RAIL_CARGO

    val badgeColor = when {
        isLostPlace -> CrimsonAlert
        isLeerstand -> Color(0xFFF59E0B)
        site == SiteType.KUNDEN_BAUSTELLE_AKTIV -> EmeraldActive
        site == SiteType.KUNDEN_BAUSTELLE_PASSIV -> CopperOrange
        site == SiteType.FIRMENSITZ -> CyanPrimary
        site == SiteType.LAGERORT -> BrassYellow
        site == SiteType.FABRIKHALLEN -> Color(0xFFEF4444)
        site == SiteType.WERKSTATT -> Color(0xFFEAB308)
        else -> Color(0xFF38BDF8)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isLostPlace) CrimsonAlert.copy(alpha = 0.5f) else SurfaceCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row: Site Type badge & Distance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(badgeColor, CircleShape)
                    )
                    Text(
                        text = if (isLostPlace) "LOST PLACE" else if (isLeerstand) "LEERSTAND" else site.displayName,
                        color = badgeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "· ${craft.displayName}",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                if (distanceStr != null) {
                    Box(
                        modifier = Modifier
                            .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = distanceStr,
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Title
            Text(
                text = place.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Address
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = place.address,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Material inventory summary pills & badges
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (placeWithMaterials.materials.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(CopperOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, CopperOrange.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "%.1f kg Material".format(placeWithMaterials.totalWeightKg),
                            color = CopperOrange,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(GoldPrecious.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, GoldPrecious.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "%,.2f €".format(placeWithMaterials.totalEstimatedValueEur),
                            color = GoldPrecious,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, CyanPrimary.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${placeWithMaterials.materials.sumOf { it.quantity }} Posten",
                            color = CyanPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                if (isRail) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0369A1).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.Train, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(10.dp))
                            Text("OSINT Rail", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (place.hasUndergroundUtility) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF78350F).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                            .border(0.5.dp, CopperOrange, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            Icon(Icons.Default.SouthEast, contentDescription = null, tint = CopperOrange, modifier = Modifier.size(10.dp))
                            Text(
                                text = if (place.undergroundCableLengthM > 0) "Erdtrasse ${place.undergroundCableLengthM.toInt()}m" else "Erdkabel",
                                color = CopperOrange,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (place.isBuilding) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E293B), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text("Gebäude", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

private fun calculateDistanceMeters(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val r = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}
