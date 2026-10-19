package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.SouthEast
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CraftType
import com.example.data.MetalType
import com.example.data.PlaceWithMaterials
import com.example.data.SiteType
import com.example.data.StorageLocation
import com.example.domain.MaterialCategorizer
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CrimsonAlert
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlaceDetailSheet(
    placeWithMaterials: PlaceWithMaterials,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onAddMaterialDirect: (
        name: String,
        metalType: MetalType,
        weightKg: Double,
        pricePerKg: Double,
        condition: String,
        notes: String,
        quantity: Int,
        storageLocation: String,
        depthMeters: Double,
        estimatedLengthMeters: Double,
        customTotalValueEur: Double?
    ) -> Unit,
    onLogUndergroundDeposit: (lengthMeters: Double, depthMeters: Double, cableType: String) -> Unit,
    onLogRailCargo: (description: String, metalType: MetalType, weightKg: Double, wagonsCount: Int) -> Unit,
    onAutoRecalculateClick: () -> Unit,
    onDeletePlaceClick: () -> Unit,
    onDeleteMaterialClick: (String) -> Unit
) {
    val context = LocalContext.current
    val place = placeWithMaterials.place
    val craft = runCatching { CraftType.valueOf(place.craftType) }.getOrDefault(CraftType.OTHER)
    val site = runCatching { SiteType.valueOf(place.siteType) }.getOrDefault(SiteType.SONSTIGES)

    // Inline logging section state
    var isLogSectionExpanded by remember { mutableStateOf(false) }
    var selectedMetal by remember { mutableStateOf(MetalType.KUPFER_MILLBERRY) }
    var materialNameInput by remember { mutableStateOf(selectedMetal.title) }
    var quantityInput by remember { mutableStateOf("1") }
    var weightInput by remember { mutableStateOf("150") }
    var pricePerKgInput by remember { mutableStateOf(selectedMetal.defaultPriceEurPerKg.toString()) }
    var customTotalValueInput by remember { mutableStateOf("") }
    var selectedStorageLocation by remember { mutableStateOf(StorageLocation.OBERIRDISCH_HALLE.displayName) }
    var depthInput by remember { mutableStateOf("1.0") }
    var lengthInput by remember { mutableStateOf("50") }
    var conditionInput by remember { mutableStateOf("Gesichtet / Vor Ort") }
    var notesInput by remember { mutableStateOf("") }

    var metalDropdownExpanded by remember { mutableStateOf(false) }
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    val parsedWeight = weightInput.toDoubleOrNull() ?: 0.0
    val parsedPrice = pricePerKgInput.toDoubleOrNull() ?: 0.0
    val parsedQuantity = quantityInput.toIntOrNull() ?: 1
    val calculatedTotalEur = customTotalValueInput.toDoubleOrNull()
        ?: (Math.round(parsedWeight * parsedPrice * 100.0) / 100.0)

    val isUndergroundSelected = selectedStorageLocation.contains("Erde", ignoreCase = true) ||
            selectedStorageLocation.contains("Erdtrasse", ignoreCase = true) ||
            selectedStorageLocation.contains("Kanal", ignoreCase = true)

    val vacancyBadgeColor = when {
        place.vacancyStatus.contains("Lost Place", ignoreCase = true) -> CrimsonAlert
        place.vacancyStatus.contains("Leerstand", ignoreCase = true) || place.vacancyStatus.contains("Verkauf", ignoreCase = true) -> Color(0xFFF59E0B)
        place.vacancyStatus.contains("Aktiv", ignoreCase = true) -> EmeraldActive
        else -> CyanPrimary
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundBlack
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Status, Title, Coordinates & Close
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Vacancy / Lost Place badge
                            Box(
                                modifier = Modifier
                                    .background(vacancyBadgeColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, vacancyBadgeColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = place.vacancyStatus.ifBlank { site.displayName },
                                    color = vacancyBadgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Craft Type Badge
                            Box(
                                modifier = Modifier
                                    .background(CopperOrange.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(craft.displayName, color = CopperOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            if (place.hasRailAccess) {
                                Box(
                                    modifier = Modifier
                                    .background(Color(0xFF0284C7).copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("OSINT RAIL / GLEIS", color = Color(0xFF38BDF8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (place.hasUndergroundUtility) {
                                Box(
                                    modifier = Modifier
                                    .background(Color(0xFF78350F).copy(alpha = 0.35f), RoundedCornerShape(4.dp))
                                    .border(0.5.dp, CopperOrange, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("ERDTRASSEN / KABEL", color = CopperOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
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
                            text = "GPS: %.5f, %.5f · %s".format(place.lat, place.lon, if (place.isBuilding) "Reines Gebäude" else "Industriefläche"),
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextMuted)
                    }
                }
            }

            // Summary Totals Banner
            item {
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
                        Text("METALLBESTAND", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
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
                            .width(1.dp)
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

                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .width(1.dp)
                            .background(SurfaceCardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("POSTEN / MENGE", color = TextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            "${placeWithMaterials.materials.sumOf { it.quantity }} Stk.",
                            color = CyanPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // OSINT Rail / Cargo Card (if rail access exists or rail notes present)
            if (place.hasRailAccess || place.railNotes.isNotBlank() || craft == CraftType.RAIL_CARGO) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF031E38), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.Train, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "OSINT RAIL & CARGO GÜTERVERKEHR",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF0284C7).copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("Gleisaufklärung", color = Color.White, fontSize = 9.sp)
                                }
                            }

                            Text(
                                text = place.railNotes.ifBlank { "Industriegleis / Anschluss mit Oberleitung Ri100 & Verladestation." },
                                color = TextPrimary,
                                fontSize = 12.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        onLogRailCargo(
                                            "Waggonladung Misch- & Buntmetallschrott (E-Wagen)",
                                            MetalType.MESSING_SCHWER,
                                            2500.0,
                                            1
                                        )
                                        Toast.makeText(context, "Rail Cargo Schrottwaggon erfasst!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF38BDF8)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0284C7))
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("+ Schrott-Waggon", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = {
                                        onLogRailCargo(
                                            "Bahn-Oberleitungskupfer Ri100 (100mm² Volldraht)",
                                            MetalType.KUPFER_MILLBERRY,
                                            890.0,
                                            1
                                        )
                                        Toast.makeText(context, "Oberleitungskupfer erfasst!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CopperOrange),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CopperOrange)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Text("+ Oberleitung (Cu)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Underground Utility / Legacy Cable Deposit Card
            if (place.hasUndergroundUtility || place.undergroundCableLengthM > 0) {
                item {
                    val length = if (place.undergroundCableLengthM > 0) place.undergroundCableLengthM else 200.0
                    val (estWeight, estValue) = MaterialCategorizer.calculateUndergroundCopper(length)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF271306), RoundedCornerShape(8.dp))
                            .border(1.dp, CopperOrange.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.SouthEast, contentDescription = null, tint = CopperOrange, modifier = Modifier.size(16.dp))
                                    Text(
                                        text = "ALTBESTÄNDE IM ERDREICH & TIEFBAU",
                                        color = CopperOrange,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .background(CopperOrange.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text("Bodenleitungsnetz", color = CopperOrange, fontSize = 9.sp)
                                }
                            }

                            Text(
                                text = place.undergroundCableType.ifBlank { "Stillgelegte 10kV Bleimantel-Kupferkabeltrasse (3x185mm² Cu) im Erdreich" },
                                color = TextPrimary,
                                fontSize = 12.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Trassenlänge: ${length.toInt()}m · Cu-Ausbeute: ca. ${estWeight.toInt()} kg",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Wert: %,.0f €".format(estValue),
                                    color = GoldPrecious,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Button(
                                onClick = {
                                    onLogUndergroundDeposit(
                                        length,
                                        1.1,
                                        place.undergroundCableType.ifBlank { "10kV Bleimantel-Altkabel (3x185mm² Cu)" }
                                    )
                                    Toast.makeText(context, "Erdtrassen-Kupfer ins Protokoll übernommen!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(36.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CopperOrange, contentColor = Color.White)
                            ) {
                                Text(
                                    "⚡ ERDKABEL-ALTBESTAND INS PROTOKOLL ÜBERNEHMEN",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // PRIMARY NEW UI SECTION: In-Place Material Logging Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceCard, RoundedCornerShape(8.dp))
                        .border(1.dp, if (isLogSectionExpanded) CyanPrimary else SurfaceCardBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Section Header with Expand / Collapse Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isLogSectionExpanded = !isLogSectionExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.PostAdd, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(20.dp))
                                Column {
                                    Text(
                                        text = "MATERIAL PROTOKOLLIEREN & LOGGEN",
                                        color = CyanPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Buntmetalle, Kabel, Werkzeuge erfassen (Gewicht, Wert, Menge)",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isLogSectionExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = CyanPrimary
                            )
                        }

                        // Collapsible Form
                        AnimatedVisibility(
                            visible = isLogSectionExpanded,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HorizontalDivider(color = SurfaceCardBorder)

                                // Quick Material Type Dropdown Selector
                                Text("Werkstoff / Metallkategorie:", color = TextSecondary, fontSize = 11.sp)
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(SurfaceHighlight, RoundedCornerShape(6.dp))
                                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                                            .clickable { metalDropdownExpanded = true }
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(selectedMetal.title, color = CopperOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Richtwert: %.2f €/kg".format(selectedMetal.defaultPriceEurPerKg), color = TextMuted, fontSize = 10.sp)
                                        }
                                    }

                                    DropdownMenu(
                                        expanded = metalDropdownExpanded,
                                        onDismissRequest = { metalDropdownExpanded = false },
                                        modifier = Modifier.background(SurfaceCard)
                                    ) {
                                        MetalType.entries.forEach { metal ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(metal.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                                        Text("Preis: %.2f €/kg · Gruppe: %s".format(metal.defaultPriceEurPerKg, metal.group), color = TextMuted, fontSize = 10.sp)
                                                    }
                                                },
                                                onClick = {
                                                    selectedMetal = metal
                                                    materialNameInput = metal.title
                                                    pricePerKgInput = metal.defaultPriceEurPerKg.toString()
                                                    customTotalValueInput = ""
                                                    metalDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Custom Name Field
                                OutlinedTextField(
                                    value = materialNameInput,
                                    onValueChange = { materialNameInput = it },
                                    label = { Text("Bezeichnung / Posten", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceHighlight,
                                        unfocusedContainerColor = SurfaceHighlight,
                                        focusedBorderColor = CyanPrimary,
                                        unfocusedBorderColor = SurfaceCardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                // Three Columns: Menge (Quantity), Gewicht (kg), Preis (€/kg)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    OutlinedTextField(
                                        value = quantityInput,
                                        onValueChange = { quantityInput = it },
                                        label = { Text("Menge (Stk)", fontSize = 10.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        modifier = Modifier.weight(0.9f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceHighlight,
                                            unfocusedContainerColor = SurfaceHighlight,
                                            focusedBorderColor = CyanPrimary,
                                            unfocusedBorderColor = SurfaceCardBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )

                                    OutlinedTextField(
                                        value = weightInput,
                                        onValueChange = { weightInput = it },
                                        label = { Text("Gewicht (kg)", fontSize = 10.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1.1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceHighlight,
                                            unfocusedContainerColor = SurfaceHighlight,
                                            focusedBorderColor = CopperOrange,
                                            unfocusedBorderColor = SurfaceCardBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )

                                    OutlinedTextField(
                                        value = pricePerKgInput,
                                        onValueChange = {
                                            pricePerKgInput = it
                                            customTotalValueInput = ""
                                        },
                                        label = { Text("Preis (€/kg)", fontSize = 10.sp) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = SurfaceHighlight,
                                            unfocusedContainerColor = SurfaceHighlight,
                                            focusedBorderColor = CopperOrange,
                                            unfocusedBorderColor = SurfaceCardBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        )
                                    )
                                }

                                // Storage Location / Fundstelle Selector
                                Text("Lagerort & Fundstelle:", color = TextSecondary, fontSize = 11.sp)
                                Box {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(SurfaceHighlight, RoundedCornerShape(6.dp))
                                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                                            .clickable { locationDropdownExpanded = true }
                                            .padding(10.dp)
                                    ) {
                                        Text(selectedStorageLocation, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }

                                    DropdownMenu(
                                        expanded = locationDropdownExpanded,
                                        onDismissRequest = { locationDropdownExpanded = false },
                                        modifier = Modifier.background(SurfaceCard)
                                    ) {
                                        StorageLocation.entries.forEach { loc ->
                                            DropdownMenuItem(
                                                text = { Text(loc.displayName, color = TextPrimary, fontSize = 12.sp) },
                                                onClick = {
                                                    selectedStorageLocation = loc.displayName
                                                    locationDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                // Extra fields if Underground / Trasse is selected
                                if (isUndergroundSelected) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = depthInput,
                                            onValueChange = { depthInput = it },
                                            label = { Text("Tiefe (Meter)", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = SurfaceHighlight,
                                                unfocusedContainerColor = SurfaceHighlight,
                                                focusedBorderColor = CopperOrange,
                                                unfocusedBorderColor = SurfaceCardBorder,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )

                                        OutlinedTextField(
                                            value = lengthInput,
                                            onValueChange = { lengthInput = it },
                                            label = { Text("Trassenlänge (m)", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = SurfaceHighlight,
                                                unfocusedContainerColor = SurfaceHighlight,
                                                focusedBorderColor = CopperOrange,
                                                unfocusedBorderColor = SurfaceCardBorder,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )

                                        Button(
                                            onClick = {
                                                val l = lengthInput.toDoubleOrNull() ?: 50.0
                                                val w = Math.round(l * 5.2 * 10.0) / 10.0
                                                weightInput = w.toString()
                                                Toast.makeText(context, "Kupfergewicht berechnet: $w kg (5.2 kg/m)", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier
                                                .weight(1.1f)
                                                .height(54.dp)
                                                .padding(top = 8.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = CopperOrange.copy(alpha = 0.85f), contentColor = Color.White)
                                        ) {
                                            Text("Berechnen (5.2 kg/m)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // Live Total Value Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(GoldPrecious.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .border(1.dp, GoldPrecious.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "BERECHNETER GESAMTWERT",
                                                color = GoldPrecious,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "%,.2f €".format(calculatedTotalEur),
                                                color = Color.White,
                                                fontSize = 17.sp,
                                                fontWeight = FontWeight.Black,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        OutlinedTextField(
                                            value = customTotalValueInput,
                                            onValueChange = { customTotalValueInput = it },
                                            label = { Text("Wert manuell anpassen (€)", fontSize = 9.sp) },
                                            placeholder = { Text("%.2f".format(calculatedTotalEur), fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                            singleLine = true,
                                            modifier = Modifier.width(160.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = SurfaceCard,
                                                unfocusedContainerColor = SurfaceCard,
                                                focusedBorderColor = GoldPrecious,
                                                unfocusedBorderColor = SurfaceCardBorder,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary
                                            )
                                        )
                                    }
                                }

                                // Condition & Notes
                                OutlinedTextField(
                                    value = conditionInput,
                                    onValueChange = { conditionInput = it },
                                    label = { Text("Zustand (z.B. Altbestand im Erdreich, Demontiert, Gelagert)", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceHighlight,
                                        unfocusedContainerColor = SurfaceHighlight,
                                        focusedBorderColor = CyanPrimary,
                                        unfocusedBorderColor = SurfaceCardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                OutlinedTextField(
                                    value = notesInput,
                                    onValueChange = { notesInput = it },
                                    label = { Text("Notizen & Details", fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = SurfaceHighlight,
                                        unfocusedContainerColor = SurfaceHighlight,
                                        focusedBorderColor = CyanPrimary,
                                        unfocusedBorderColor = SurfaceCardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                // Submit Log Button
                                Button(
                                    onClick = {
                                        val depth = depthInput.toDoubleOrNull() ?: 0.0
                                        val len = lengthInput.toDoubleOrNull() ?: 0.0
                                        val customVal = customTotalValueInput.toDoubleOrNull()

                                        onAddMaterialDirect(
                                            materialNameInput,
                                            selectedMetal,
                                            parsedWeight,
                                            parsedPrice,
                                            conditionInput,
                                            notesInput,
                                            parsedQuantity,
                                            selectedStorageLocation,
                                            depth,
                                            len,
                                            customVal
                                        )

                                        Toast.makeText(context, "$materialNameInput erfolgreich erfasst!", Toast.LENGTH_SHORT).show()
                                        // Reset inputs
                                        notesInput = ""
                                        customTotalValueInput = ""
                                        isLogSectionExpanded = false
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary, contentColor = Color.Black)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text(
                                        "MATERIAL JETZT PROTOKOLLIEREN",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions: Auto-Scout, Route, Delete
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
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
            }

            // Materials Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PROTOKOLLIERTE MATERIALIEN & METALLE (${placeWithMaterials.materials.size})",
                        color = CyanPrimary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Gesamt: ${placeWithMaterials.materials.sumOf { it.quantity }} Stk.",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Materials Items List
            if (placeWithMaterials.materials.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Noch keine Metalle erfasst.\nÖffne oben 'MATERIAL PROTOKOLLIEREN' oder tippe auf 'Auto-Scout'.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
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

            // Place Notes
            if (place.notes.isNotBlank()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .padding(10.dp)
                    ) {
                        Text("Objekt-Notizen: ${place.notes}", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            // Delete Place Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onDeletePlaceClick)
                        .padding(vertical = 8.dp),
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

                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}
