package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CraftType
import com.example.data.PlaceWithMaterials
import com.example.data.SiteType
import com.example.ui.theme.BackgroundBlack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailSheet(
    placeWithMaterials: PlaceWithMaterials,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddMaterialClick: () -> Unit,
    onAutoRecalculateClick: () -> Unit,
    onDeletePlaceClick: () -> Unit,
    onDeleteMaterialClick: (String) -> Unit
) {
    val context = LocalContext.current
    val place = placeWithMaterials.place
    val craft = runCatching { CraftType.valueOf(place.craftType) }.getOrDefault(CraftType.OTHER)
    val site = runCatching { SiteType.valueOf(place.siteType) }.getOrDefault(SiteType.SONSTIGES)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(CyanPrimary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(site.displayName, color = CyanPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .background(CopperOrange.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(craft.displayName, color = CopperOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = place.name,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = place.address,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Koordinaten: %.5f, %.5f".format(place.lat, place.lon),
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextMuted)
                }
            }

            // Summary Aggregate Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, RoundedCornerShape(8.dp))
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("GESAMTGEWICHT", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        "%.1f kg".format(placeWithMaterials.totalWeightKg),
                        color = CopperOrange,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Box(
                    modifier = Modifier
                        .height(30.dp)
                        .padding(horizontal = 1.dp)
                        .background(SurfaceCardBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("GESAMTWERT (EST.)", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        "%,.2f €".format(placeWithMaterials.totalEstimatedValueEur),
                        color = GoldPrecious,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick Actions: Add Material, Auto-Predict, Navigate, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = onAddMaterialClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("+ Material", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                }

                OutlinedButton(
                    onClick = onAutoRecalculateClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CopperOrange),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CopperOrange)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Auto-Scout", fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 4.dp))
                }

                OutlinedButton(
                    onClick = {
                        val gmmIntentUri = Uri.parse("geo:${place.lat},${place.lon}?q=${place.lat},${place.lon}(${Uri.encode(place.name)})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=${place.lat},${place.lon}"))
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceCardBorder)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("Route", fontSize = 11.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }

            HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 2.dp))

            // Materials & Inventories Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "GESICHTETES MATERIAL & METALLE (${placeWithMaterials.materials.size})",
                    color = CyanPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (place.isBuilding) "Reines Gebäude" else "Freifläche",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            // Material Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (placeWithMaterials.materials.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Noch keine Metalle erfasst. Tippe auf '+ Material' oder 'Auto-Scout'.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                } else {
                    items(placeWithMaterials.materials) { material ->
                        MaterialItemRow(
                            item = material,
                            onDeleteClick = { onDeleteMaterialClick(material.id) }
                        )
                    }
                }
            }

            if (place.notes.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text("Hinweise: ${place.notes}", color = TextSecondary, fontSize = 11.sp)
                }
            }

            // Delete Place Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onDeletePlaceClick)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = CrimsonAlert, modifier = Modifier.size(16.dp))
                Text(
                    "Diesen Standort löschen",
                    color = CrimsonAlert,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
