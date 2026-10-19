package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Factory
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.ChangePinDialog
import com.example.ui.KreyosViewModel
import com.example.ui.NavigationTab
import com.example.ui.PinLockScreen
import com.example.ui.components.AddMaterialDialog
import com.example.ui.components.AddPlaceDialog
import com.example.ui.components.DaylightBar
import com.example.ui.components.KreyosDrawerContent
import com.example.ui.components.KreyosTopBar
import com.example.ui.components.PlaceDetailSheet
import com.example.ui.screens.BaustellenScreen
import com.example.ui.screens.GewerbeScreen
import com.example.ui.screens.HandwerkScreen
import com.example.ui.screens.RadarMapScreen
import com.example.ui.screens.ValuableMaterialsScreen
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                var isUnlocked by remember { mutableStateOf(false) }

                if (!isUnlocked) {
                    PinLockScreen(onUnlocked = { isUnlocked = true })
                } else {
                    KreyosApp(onLockApp = { isUnlocked = false })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KreyosApp(
    viewModel: KreyosViewModel = viewModel(),
    onLockApp: () -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val activeTab by viewModel.activeTab.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val filteredPlaces by viewModel.filteredPlaces.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()
    val scanStatus by viewModel.scanStatus.collectAsState()
    val userLocation by viewModel.currentLocation.collectAsState()
    val sunTimes by viewModel.sunTimes.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val showAddPlaceDialog by viewModel.showAddPlaceDialog.collectAsState()
    val placeForAddingMaterial by viewModel.placeForAddingMaterial.collectAsState()
    var showChangePinDialog by remember { mutableStateOf(false) }

    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateLocation(loc.latitude, loc.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // Handled gracefully
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasFine) {
            locationPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        } else {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    if (loc != null) {
                        viewModel.updateLocation(loc.latitude, loc.longitude)
                    }
                }
            } catch (e: SecurityException) {}
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            KreyosDrawerContent(
                activeTab = activeTab,
                onTabSelected = { tab ->
                    viewModel.setActiveTab(tab)
                    viewModel.setCraftFilter(null)
                },
                onSelectCraftPreset = { craft ->
                    viewModel.setCraftFilter(craft)
                },
                onAddPlaceClick = {
                    viewModel.showAddPlace(true)
                },
                onExportJsonClick = {
                    val json = viewModel.exportPlacesJson()
                    clipboardManager.setText(AnnotatedString(json))
                    Toast.makeText(context, "JSON in Zwischenablage kopiert!", Toast.LENGTH_LONG).show()
                },
                onLockClick = {
                    coroutineScope.launch { drawerState.close() }
                    onLockApp()
                },
                onChangePinClick = {
                    showChangePinDialog = true
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                },
                totalPlacesCount = filteredPlaces.size,
                totalWeightKg = filteredPlaces.sumOf { it.totalWeightKg },
                totalValueEur = filteredPlaces.sumOf { it.totalEstimatedValueEur }
            )
        }
    ) {
        Scaffold(
            topBar = {
                KreyosTopBar(
                    filterState = filterState,
                    isScanning = isScanning,
                    onMenuClick = {
                        coroutineScope.launch { drawerState.open() }
                    },
                    onScanClick = {
                        viewModel.runOverpassScan()
                    },
                    onSearchChange = { q ->
                        viewModel.setSearchQuery(q)
                    },
                    onToggleOnlyBuildings = {
                        viewModel.toggleOnlyBuildings()
                    },
                    onToggleOnlyUtility = {
                        viewModel.toggleOnlyUtility()
                    },
                    onToggleOnlyValuable = {
                        viewModel.toggleOnlyValuableMetals()
                    }
                )
            },
            bottomBar = {
                Column {
                    if (activeTab != NavigationTab.KARTE) {
                        DaylightBar(sunTimes = sunTimes)
                    }

                    NavigationBar(
                        containerColor = BackgroundBlack,
                        contentColor = CyanPrimary,
                        modifier = Modifier.border(1.dp, SurfaceCardBorder)
                    ) {
                        NavigationBarItem(
                            selected = activeTab == NavigationTab.GEWERBE,
                            onClick = { viewModel.setActiveTab(NavigationTab.GEWERBE) },
                            icon = { Icon(Icons.Default.Factory, contentDescription = null) },
                            label = { Text("Gewerbe", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyanPrimary,
                                indicatorColor = CyanPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.HANDWERK,
                            onClick = { viewModel.setActiveTab(NavigationTab.HANDWERK) },
                            icon = { Icon(Icons.Default.Handyman, contentDescription = null) },
                            label = { Text("Handwerk", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyanPrimary,
                                indicatorColor = CyanPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.BAUSTELLEN,
                            onClick = { viewModel.setActiveTab(NavigationTab.BAUSTELLEN) },
                            icon = { Icon(Icons.Default.Construction, contentDescription = null) },
                            label = { Text("Baustellen", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyanPrimary,
                                indicatorColor = CyanPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.WERTSTOFFE,
                            onClick = { viewModel.setActiveTab(NavigationTab.WERTSTOFFE) },
                            icon = { Icon(Icons.Default.Diamond, contentDescription = null) },
                            label = { Text("Metalle", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyanPrimary,
                                indicatorColor = CyanPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            )
                        )

                        NavigationBarItem(
                            selected = activeTab == NavigationTab.KARTE,
                            onClick = { viewModel.setActiveTab(NavigationTab.KARTE) },
                            icon = { Icon(Icons.Default.Map, contentDescription = null) },
                            label = { Text("Radar", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = CyanPrimary,
                                indicatorColor = CyanPrimary,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextMuted
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddPlace(true) },
                    containerColor = CyanPrimary,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Standort hinzufügen", modifier = Modifier.size(24.dp))
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundBlack)
            ) {
                when (activeTab) {
                    NavigationTab.GEWERBE -> {
                        GewerbeScreen(
                            places = filteredPlaces,
                            userLocation = userLocation,
                            onPlaceClick = { place -> viewModel.selectPlace(place) }
                        )
                    }
                    NavigationTab.HANDWERK -> {
                        HandwerkScreen(
                            places = filteredPlaces,
                            userLocation = userLocation,
                            selectedCraftFilter = filterState.craftFilter,
                            onCraftFilterChange = { craft -> viewModel.setCraftFilter(craft) },
                            onPlaceClick = { place -> viewModel.selectPlace(place) }
                        )
                    }
                    NavigationTab.BAUSTELLEN -> {
                        BaustellenScreen(
                            places = filteredPlaces,
                            userLocation = userLocation,
                            onPlaceClick = { place -> viewModel.selectPlace(place) }
                        )
                    }
                    NavigationTab.WERTSTOFFE -> {
                        ValuableMaterialsScreen(
                            places = filteredPlaces,
                            userLocation = userLocation,
                            onPlaceClick = { place -> viewModel.selectPlace(place) }
                        )
                    }
                    NavigationTab.KARTE -> {
                        RadarMapScreen(
                            places = filteredPlaces,
                            centerLocation = userLocation,
                            sunTimes = sunTimes,
                            isScanning = isScanning,
                            scanStatus = scanStatus,
                            onRunScan = { viewModel.runOverpassScan() },
                            onPlaceClick = { place -> viewModel.selectPlace(place) }
                        )
                    }
                }
            }
        }
    }

    // Place Detail Bottom Sheet
    selectedPlace?.let { placeWithMaterials ->
        PlaceDetailSheet(
            placeWithMaterials = placeWithMaterials,
            sheetState = detailSheetState,
            onDismiss = { viewModel.selectPlace(null) },
            onAddMaterialDirect = { name, metalType, weightKg, pricePerKg, condition, notes, quantity, storageLocation, depthMeters, estimatedLengthMeters, customTotalValueEur ->
                viewModel.addMaterialToPlace(
                    placeId = placeWithMaterials.place.id,
                    name = name,
                    metalType = metalType,
                    weightKg = weightKg,
                    pricePerKg = pricePerKg,
                    condition = condition,
                    notes = notes,
                    quantity = quantity,
                    storageLocation = storageLocation,
                    depthMeters = depthMeters,
                    estimatedLengthMeters = estimatedLengthMeters,
                    customTotalValueEur = customTotalValueEur
                )
            },
            onLogUndergroundDeposit = { lengthMeters, depthMeters, cableType ->
                viewModel.logUndergroundCableDeposit(
                    placeId = placeWithMaterials.place.id,
                    cableLengthMeters = lengthMeters,
                    depthMeters = depthMeters,
                    cableType = cableType
                )
            },
            onLogRailCargo = { description, metalType, weightKg, wagonsCount ->
                viewModel.addMaterialToPlace(
                    placeId = placeWithMaterials.place.id,
                    name = description,
                    metalType = metalType,
                    weightKg = weightKg,
                    pricePerKg = metalType.defaultPriceEurPerKg,
                    condition = "OSINT Güterverkehr / Gleisverladung",
                    notes = "Aufklärung Güterverkehr: $wagonsCount Waggon(s)/Ladestelle.",
                    quantity = wagonsCount,
                    storageLocation = "Gleisbett / Laderampe"
                )
            },
            onAutoRecalculateClick = { viewModel.triggerAutoMaterialGeneration(placeWithMaterials.place.id) },
            onDeletePlaceClick = { viewModel.deletePlace(placeWithMaterials.place.id) },
            onDeleteMaterialClick = { matId -> viewModel.deleteMaterial(matId) }
        )
    }

    // Add Place Dialog
    if (showAddPlaceDialog) {
        AddPlaceDialog(
            onDismiss = { viewModel.showAddPlace(false) },
            onConfirm = { name, category, craftType, siteType, isBuilding, hasUtility, address, notes, vacancyStatus, hasRailAccess, railNotes, cableLen, cableType ->
                viewModel.addNewPlace(
                    name = name,
                    category = category,
                    craftType = craftType,
                    siteType = siteType,
                    isBuilding = isBuilding,
                    hasUndergroundUtility = hasUtility,
                    address = address,
                    notes = notes,
                    vacancyStatus = vacancyStatus,
                    hasRailAccess = hasRailAccess,
                    railNotes = railNotes,
                    undergroundCableLengthM = cableLen,
                    undergroundCableType = cableType
                )
            }
        )
    }

    // Add Material Dialog
    placeForAddingMaterial?.let { placeWithMaterials ->
        AddMaterialDialog(
            placeName = placeWithMaterials.place.name,
            onDismiss = { viewModel.setPlaceForAddingMaterial(null) },
            onConfirm = { name, metalType, weightKg, pricePerKg, condition, notes ->
                viewModel.addMaterialToPlace(
                    placeId = placeWithMaterials.place.id,
                    name = name,
                    metalType = metalType,
                    weightKg = weightKg,
                    pricePerKg = pricePerKg,
                    condition = condition,
                    notes = notes
                )
            }
        )
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
        ChangePinDialog(
            onDismiss = { showChangePinDialog = false },
            onPinChanged = {
                Toast.makeText(context, "Sicherheits-PIN erfolgreich aktualisiert!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
