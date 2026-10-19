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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MetalType
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddMaterialDialog(
    placeName: String,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        metalType: MetalType,
        weightKg: Double,
        pricePerKg: Double,
        condition: String,
        notes: String
    ) -> Unit
) {
    var selectedMetal by remember { mutableStateOf(MetalType.KUPFER_MILLBERRY) }
    var customName by remember { mutableStateOf(selectedMetal.title) }
    var weightInput by remember { mutableStateOf("150") }
    var priceInput by remember { mutableStateOf(selectedMetal.defaultPriceEurPerKg.toString()) }
    var condition by remember { mutableStateOf("Gesichtet / Lagerbestand") }
    var notes by remember { mutableStateOf("") }

    var metalMenuExpanded by remember { mutableStateOf(false) }

    val weight = weightInput.toDoubleOrNull() ?: 0.0
    val price = priceInput.toDoubleOrNull() ?: 0.0
    val calculatedTotal = Math.round(weight * price * 100.0) / 100.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = BackgroundBlack,
            border = androidx.compose.foundation.BorderStroke(1.dp, CopperOrange),
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
                            text = "MATERIAL ERFASSEN",
                            color = CopperOrange,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = placeName,
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Schließen", tint = TextMuted)
                    }
                }

                // Metal Category Selector
                Text("Buntmetall / Werkstoff:", color = TextSecondary, fontSize = 11.sp)
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceCard, RoundedCornerShape(6.dp))
                            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                            .clickable { metalMenuExpanded = true }
                            .padding(12.dp)
                    ) {
                        Text(selectedMetal.title, color = CopperOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    DropdownMenu(
                        expanded = metalMenuExpanded,
                        onDismissRequest = { metalMenuExpanded = false },
                        modifier = Modifier.background(SurfaceCard)
                    ) {
                        MetalType.entries.forEach { metal ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(metal.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "Richtwert: %.2f €/kg".format(metal.defaultPriceEurPerKg),
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                },
                                onClick = {
                                    selectedMetal = metal
                                    customName = metal.title
                                    priceInput = metal.defaultPriceEurPerKg.toString()
                                    metalMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Custom Name
                OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Bezeichnung / Posten", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = CopperOrange,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Weight & Price per kg
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("Gewicht (kg)", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = CopperOrange,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("Preis (€/kg)", fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = SurfaceCard,
                            unfocusedContainerColor = SurfaceCard,
                            focusedBorderColor = CopperOrange,
                            unfocusedBorderColor = SurfaceCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }

                // Live Value Calculation Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GoldPrecious.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                        .border(1.dp, GoldPrecious, RoundedCornerShape(6.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "BERECHNETER GESAMTWERT",
                            color = GoldPrecious,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "%,.2f €".format(calculatedTotal),
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Condition
                OutlinedTextField(
                    value = condition,
                    onValueChange = { condition = it },
                    label = { Text("Zustand / Lage (z.B. Demontiert, Im Schuppen)", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = CopperOrange,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Zusätzliche Bemerkungen", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedBorderColor = CopperOrange,
                        unfocusedBorderColor = SurfaceCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                Button(
                    onClick = {
                        onConfirm(
                            customName,
                            selectedMetal,
                            weight,
                            price,
                            condition,
                            notes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CopperOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        "MATERIAL EINTRAGEN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
