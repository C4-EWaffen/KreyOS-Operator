package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CraftType
import com.example.ui.NavigationTab
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.BrassYellow
import com.example.ui.theme.CopperOrange
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.EmeraldActive
import com.example.ui.theme.GoldPrecious
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun KreyosDrawerContent(
    activeTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onSelectCraftPreset: (CraftType?) -> Unit,
    onAddPlaceClick: () -> Unit,
    onExportJsonClick: () -> Unit,
    onCloseDrawer: () -> Unit,
    totalPlacesCount: Int,
    totalWeightKg: Double,
    totalValueEur: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(BackgroundBlack)
            .border(width = 1.dp, color = SurfaceCardBorder)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Drawer Header with Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "KREY",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "OS",
                        color = CyanPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.5.sp
                    )
                }
                Text(
                    text = "STEUERUNGSMENÜ · KREY-VERSIUM",
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            IconButton(
                onClick = onCloseDrawer,
                modifier = Modifier
                    .size(36.dp)
                    .background(SurfaceCard, RoundedCornerShape(6.dp))
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Menü schließen",
                    tint = CyanPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Live Stats Dashboard Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(8.dp))
                .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "ERFASSTE BESTÄNDE",
                    color = CyanPrimary,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Standorte:", color = TextSecondary, fontSize = 12.sp)
                    Text("$totalPlacesCount", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Metallgewicht:", color = TextSecondary, fontSize = 12.sp)
                    Text("%.1f kg".format(totalWeightKg), color = CopperOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Gesamtwert:", color = TextSecondary, fontSize = 12.sp)
                    Text("%,.2f €".format(totalValueEur), color = GoldPrecious, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            text = "HAUPTSEKTIONEN",
            color = TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(top = 6.dp)
        )

        // Main Navigation Items
        DrawerItem(
            title = "Gewerbeimmobilien",
            subtitle = "Fabriken, Hallen, Werkstätten (keine Wohnungen)",
            icon = Icons.Default.Factory,
            selected = activeTab == NavigationTab.GEWERBE,
            onClick = {
                onTabSelected(NavigationTab.GEWERBE)
                onCloseDrawer()
            }
        )

        DrawerItem(
            title = "Handwerksbetriebe",
            subtitle = "Dachdecker, Installateure, Elektriker, Kabelwerke",
            icon = Icons.Default.Handyman,
            selected = activeTab == NavigationTab.HANDWERK,
            onClick = {
                onTabSelected(NavigationTab.HANDWERK)
                onCloseDrawer()
            }
        )

        DrawerItem(
            title = "Kunden-Baustellen",
            subtitle = "Aktiv vs. Passiv & gesichtetes Material",
            icon = Icons.Default.Construction,
            selected = activeTab == NavigationTab.BAUSTELLEN,
            onClick = {
                onTabSelected(NavigationTab.BAUSTELLEN)
                onCloseDrawer()
            }
        )

        DrawerItem(
            title = "Wertstoffe & Metalle",
            subtitle = "Kupfer, Messing, Edelmetalle, Rechner",
            icon = Icons.Default.Diamond,
            selected = activeTab == NavigationTab.WERTSTOFFE,
            onClick = {
                onTabSelected(NavigationTab.WERTSTOFFE)
                onCloseDrawer()
            }
        )

        DrawerItem(
            title = "Radar-Karte & Peilung",
            subtitle = "Sonnendaten, Entfernungen & Kompass",
            icon = Icons.Default.Map,
            selected = activeTab == NavigationTab.KARTE,
            onClick = {
                onTabSelected(NavigationTab.KARTE)
                onCloseDrawer()
            }
        )

        HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 4.dp))

        Text(
            text = "SCHNELLFILTER GEWERBEARTEN",
            color = TextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        CraftFilterRow(name = "Alle Gewerbe anzeigen", icon = Icons.Default.Business, color = CyanPrimary) {
            onSelectCraftPreset(null)
            onTabSelected(NavigationTab.HANDWERK)
            onCloseDrawer()
        }

        CraftFilterRow(name = "Kabelwerke & Draht", icon = Icons.Default.Cable, color = CopperOrange) {
            onSelectCraftPreset(CraftType.CABLE_WORKS)
            onTabSelected(NavigationTab.HANDWERK)
            onCloseDrawer()
        }

        CraftFilterRow(name = "Messing- & CU Werkstätten", icon = Icons.Default.Build, color = BrassYellow) {
            onSelectCraftPreset(CraftType.BRASS_WORKSHOP)
            onTabSelected(NavigationTab.HANDWERK)
            onCloseDrawer()
        }

        CraftFilterRow(name = "Dachdeckerbetriebe", icon = Icons.Default.Roofing, color = CopperOrange) {
            onSelectCraftPreset(CraftType.ROOFER)
            onTabSelected(NavigationTab.HANDWERK)
            onCloseDrawer()
        }

        CraftFilterRow(name = "Gas- & Wasserinstallateure", icon = Icons.Default.Plumbing, color = CyanPrimary) {
            onSelectCraftPreset(CraftType.PLUMBER)
            onTabSelected(NavigationTab.HANDWERK)
            onCloseDrawer()
        }

        CraftFilterRow(name = "Elektriker & Schaltschrankbau", icon = Icons.Default.ElectricBolt, color = GoldPrecious) {
            onSelectCraftPreset(CraftType.ELECTRICIAN)
            onTabSelected(NavigationTab.HANDWERK)
            onCloseDrawer()
        }

        HorizontalDivider(color = SurfaceCardBorder, modifier = Modifier.padding(vertical = 4.dp))

        // Actions: Add place & Export
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyanPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                .border(1.dp, CyanPrimary, RoundedCornerShape(6.dp))
                .clickable {
                    onAddPlaceClick()
                    onCloseDrawer()
                }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = CyanPrimary, modifier = Modifier.size(18.dp))
            Text("Neuen Standort anlegen", color = CyanPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(6.dp))
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(6.dp))
                .clickable {
                    onExportJsonClick()
                    onCloseDrawer()
                }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
            Text("Daten als JSON exportieren", color = TextSecondary, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrawerItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) CyanPrimary.copy(alpha = 0.15f) else SurfaceCard,
                RoundedCornerShape(6.dp)
            )
            .border(
                1.dp,
                if (selected) CyanPrimary else SurfaceCardBorder,
                RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) CyanPrimary else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Column {
            Text(
                text = title,
                color = if (selected) CyanPrimary else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun CraftFilterRow(
    name: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(name, color = TextSecondary, fontSize = 12.sp)
    }
}
