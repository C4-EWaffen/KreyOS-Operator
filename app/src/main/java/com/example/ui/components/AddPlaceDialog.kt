package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import com.example.data.CraftType
import com.example.data.SiteType
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddPlaceDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        category: String,
        craftType: CraftType,
        siteType: SiteType,
        isBuilding: Boolean,
        hasUndergroundUtility: Boolean,
        address: String,
        notes: String
    ) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCraft by remember { mutableStateOf(CraftType.CABLE_WORKS) }
    var selectedSiteType by remember { mutableStateOf(SiteType.LAGERORT) }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isBuilding by remember { mutableStateOf(true) }
    var hasUtility by remember { mutableStateOf(false) }

    var craftMenuExpanded by remember { mutableStateOf(false) }
    var siteMenuExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = BackgroundBlack,
            border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NEUEN STANDORT ERFASSEN",
                            color = CyanPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Gewerbe, Handwerk oder Baustelle",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextMuted)
                    }
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name / Bezeichnung", fontSize = 12.sp) },
                    placeholder = { Text("z.B. Kupfer & Kabel Zentrallager") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Craft Selection
                Text("Gewerbeart:", color = TextSecondary, fontSize = 11.sp)
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                            .clickable { craftMenuExpanded = true }
                            .padding(12.dp)
                    ) {
                        Text(selectedCraft.displayName, color = CyanPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    DropdownMenu(
                        expanded = craftMenuExpanded,
                        onDismissRequest = { craftMenuExpanded = false },
                        modifier = Modifier.background(SurfaceCard)
                    ) {
                        CraftType.entries.forEach { craft ->
                            DropdownMenuItem(
                                text = { Text(craft.displayName, color = TextPrimary, fontSize = 12.sp) },
                                onClick = {
                                    selectedCraft = craft
                                    craftMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Site Type Selection (Firmensitz vs Lagerort vs Baustelle Aktiv/Passiv)
                Text("Standort-Typ:", color = TextSecondary, fontSize = 11.sp)
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                            .clickable { siteMenuExpanded = true }
                            .padding(12.dp)
                    ) {
                        Text(selectedSiteType.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    DropdownMenu(
                        expanded = siteMenuExpanded,
                        onDismissRequest = { siteMenuExpanded = false },
                        modifier = Modifier.background(SurfaceCard)
                    ) {
                        SiteType.entries.forEach { site ->
                            DropdownMenuItem(
                                text = { Text(site.displayName, color = TextPrimary, fontSize = 12.sp) },
                                onClick = {
                                    selectedSiteType = site
                                    siteMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse / Anschrift", fontSize = 12.sp) },
                    placeholder = { Text("Musterstraße 12, 42103 Wuppertal") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Checkboxes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isBuilding = !isBuilding }
                ) {
                    Checkbox(
                        checked = isBuilding,
                        onCheckedChange = { isBuilding = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CyanPrimary,
                            checkmarkColor = Color.Black
                        )
                    )
                    Text("Reines Gebäude (Fabrik, Halle, Werkstatt)", color = TextPrimary, fontSize = 12.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { hasUtility = !hasUtility }
                ) {
                    Checkbox(
                        checked = hasUtility,
                        onCheckedChange = { hasUtility = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = CyanPrimary,
                            checkmarkColor = Color.Black
                        )
                    )
                    Text("Bodenleitungen / Leitungsnetz vorhanden", color = TextPrimary, fontSize = 12.sp)
                }

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notizen & Sichtungen (z.B. Cu, Werkzeuge)", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Button(
                    onClick = {
                        onConfirm(
                            name,
                            selectedCraft.displayName,
                            selectedCraft,
                            selectedSiteType,
                            isBuilding,
                            hasUtility,
                            address,
                            notes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        "STANDORT SPEICHERN & METALLE ERFASSEN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
