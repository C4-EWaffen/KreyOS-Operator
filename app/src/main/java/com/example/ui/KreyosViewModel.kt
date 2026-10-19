package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CraftType
import com.example.data.InitialDataSeeder
import com.example.data.KreyosDatabase
import com.example.data.MaterialItemEntity
import com.example.data.MetalType
import com.example.data.PlaceEntity
import com.example.data.PlaceWithMaterials
import com.example.data.SiteType
import com.example.domain.MaterialCategorizer
import com.example.domain.SunTimeCalculator
import com.example.domain.SunTimes
import com.example.network.OverpassApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class NavigationTab(val title: String, val badge: String) {
    GEWERBE("Gewerbe & Hallen", "Fabriken"),
    HANDWERK("Handwerk & Sitze", "Lagerorte"),
    BAUSTELLEN("Kunden-Baustellen", "Aktiv/Passiv"),
    WERTSTOFFE("Wertstoffe & Cu/Ms", "Edelmetalle"),
    KARTE("Radar-Karte", "Tageslicht")
}

data class FilterState(
    val searchQuery: String = "",
    val onlyBuildings: Boolean = false,
    val onlyUndergroundUtility: Boolean = false,
    val onlyValuableMetals: Boolean = false,
    val craftFilter: CraftType? = null,
    val siteTypeFilter: SiteType? = null
)

class KreyosViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KreyosDatabase.getInstance(application)
    private val placeDao = db.placeDao()
    private val materialDao = db.materialDao()
    private val apiClient = OverpassApiClient()

    private val _activeTab = MutableStateFlow(NavigationTab.GEWERBE)
    val activeTab: StateFlow<NavigationTab> = _activeTab.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanStatus = MutableStateFlow("Bereit für Radar-Scan.")
    val scanStatus: StateFlow<String> = _scanStatus.asStateFlow()

    private val _currentLocation = MutableStateFlow(Pair(51.2562, 7.1508))
    val currentLocation: StateFlow<Pair<Double, Double>> = _currentLocation.asStateFlow()

    private val _sunTimes = MutableStateFlow(SunTimeCalculator.calculate(51.2562, 7.1508))
    val sunTimes: StateFlow<SunTimes> = _sunTimes.asStateFlow()

    private val _selectedPlace = MutableStateFlow<PlaceWithMaterials?>(null)
    val selectedPlace: StateFlow<PlaceWithMaterials?> = _selectedPlace.asStateFlow()

    private val _showAddPlaceDialog = MutableStateFlow(false)
    val showAddPlaceDialog: StateFlow<Boolean> = _showAddPlaceDialog.asStateFlow()

    private val _placeForAddingMaterial = MutableStateFlow<PlaceWithMaterials?>(null)
    val placeForAddingMaterial: StateFlow<PlaceWithMaterials?> = _placeForAddingMaterial.asStateFlow()

    private val rawPlacesFlow = placeDao.getAllPlacesWithMaterials()

    // Filtered places stream
    val filteredPlaces: StateFlow<List<PlaceWithMaterials>> = combine(
        rawPlacesFlow,
        _activeTab,
        _filterState
    ) { places, tab, filters ->
        places.filter { item ->
            val p = item.place
            val craft = runCatching { CraftType.valueOf(p.craftType) }.getOrDefault(CraftType.OTHER)
            val site = runCatching { SiteType.valueOf(p.siteType) }.getOrDefault(SiteType.SONSTIGES)

            // Tab-specific basic routing
            val matchesTab = when (tab) {
                NavigationTab.GEWERBE -> {
                    // Commercial properties: Factories, Halls, Workshops. Exclude residential / cafes.
                    craft in listOf(CraftType.FACTORY_HALL, CraftType.BRASS_WORKSHOP, CraftType.CABLE_WORKS, CraftType.OTHER) ||
                            site in listOf(SiteType.FABRIKHALLEN, SiteType.WERKSTATT, SiteType.LAGERORT)
                }
                NavigationTab.HANDWERK -> {
                    // Trades: Roofers, Plumbers, Electricians, Cable works, Brass workshops
                    craft in listOf(CraftType.ROOFER, CraftType.PLUMBER, CraftType.ELECTRICIAN, CraftType.CABLE_WORKS, CraftType.BRASS_WORKSHOP)
                }
                NavigationTab.BAUSTELLEN -> {
                    // Customer construction sites: active vs passive
                    craft == CraftType.CONSTRUCTION || site in listOf(SiteType.KUNDEN_BAUSTELLE_AKTIV, SiteType.KUNDEN_BAUSTELLE_PASSIV)
                }
                NavigationTab.WERTSTOFFE -> {
                    // Valuable materials: has copper, brass, bronze, or precious metals
                    item.hasHighValueOrPreciousMetals || item.totalEstimatedValueEur > 500.0
                }
                NavigationTab.KARTE -> true
            }

            if (!matchesTab && tab != NavigationTab.KARTE) return@filter false

            // Building filter
            if (filters.onlyBuildings && !p.isBuilding) return@filter false

            // Utility lines filter
            if (filters.onlyUndergroundUtility && !p.hasUndergroundUtility) return@filter false

            // Valuable metals filter
            if (filters.onlyValuableMetals && !item.hasHighValueOrPreciousMetals) return@filter false

            // Craft filter
            if (filters.craftFilter != null && craft != filters.craftFilter) return@filter false

            // Site type filter
            if (filters.siteTypeFilter != null && site != filters.siteTypeFilter) return@filter false

            // Search query text matching
            if (filters.searchQuery.isNotBlank()) {
                val q = filters.searchQuery.trim().lowercase()
                val blob = "${p.name} ${p.address} ${p.category} ${p.notes} ${item.materials.joinToString(" ") { it.materialName }}".lowercase()
                if (!blob.contains(q)) return@filter false
            }

            true
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            InitialDataSeeder.seedIfEmpty(placeDao, materialDao)
            refreshSunTimes()
        }
    }

    fun setActiveTab(tab: NavigationTab) {
        _activeTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _filterState.value = _filterState.value.copy(searchQuery = query)
    }

    fun toggleOnlyBuildings() {
        _filterState.value = _filterState.value.copy(onlyBuildings = !_filterState.value.onlyBuildings)
    }

    fun toggleOnlyUtility() {
        _filterState.value = _filterState.value.copy(onlyUndergroundUtility = !_filterState.value.onlyUndergroundUtility)
    }

    fun toggleOnlyValuableMetals() {
        _filterState.value = _filterState.value.copy(onlyValuableMetals = !_filterState.value.onlyValuableMetals)
    }

    fun setCraftFilter(craft: CraftType?) {
        _filterState.value = _filterState.value.copy(craftFilter = craft)
    }

    fun setSiteTypeFilter(site: SiteType?) {
        _filterState.value = _filterState.value.copy(siteTypeFilter = site)
    }

    fun selectPlace(place: PlaceWithMaterials?) {
        _selectedPlace.value = place
    }

    fun showAddPlace(show: Boolean) {
        _showAddPlaceDialog.value = show
    }

    fun setPlaceForAddingMaterial(place: PlaceWithMaterials?) {
        _placeForAddingMaterial.value = place
    }

    fun updateLocation(lat: Double, lon: Double) {
        _currentLocation.value = Pair(lat, lon)
        refreshSunTimes(lat, lon)
    }

    fun refreshSunTimes(lat: Double = _currentLocation.value.first, lon: Double = _currentLocation.value.second) {
        _sunTimes.value = SunTimeCalculator.calculate(lat, lon)
    }

    fun runOverpassScan(radiusMeters: Int = 3000) {
        if (_isScanning.value) return
        viewModelScope.launch {
            _isScanning.value = true
            _scanStatus.value = "Radar-Scan aktiv: Suche Gewerbe, Hallen & Handwerksbetriebe..."
            try {
                val (lat, lon) = _currentLocation.value
                val result = apiClient.scanCommercialAndCrafts(
                    lat = lat,
                    lon = lon,
                    radiusMeters = radiusMeters,
                    onlyBuildings = _filterState.value.onlyBuildings,
                    onlyWithUndergroundUtility = _filterState.value.onlyUndergroundUtility
                )
                placeDao.insertPlaces(result.places)
                materialDao.insertMaterials(result.materials)
                _scanStatus.value = "Scan abgeschlossen (${result.endpointUsed}): ${result.places.size} Objekte & ${result.materials.size} Materialien erfasst in ${result.durationMs}ms."
            } catch (e: Exception) {
                _scanStatus.value = "Scan-Fehler: ${e.message ?: "Verbindungsfehler"}"
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun addNewPlace(
        name: String,
        category: String,
        craftType: CraftType,
        siteType: SiteType,
        isBuilding: Boolean,
        hasUndergroundUtility: Boolean,
        address: String,
        notes: String,
        lat: Double = _currentLocation.value.first,
        lon: Double = _currentLocation.value.second,
        markerShape: String = "circle",
        markerColor: String = "#00D9FF"
    ) {
        viewModelScope.launch {
            val placeId = "pl_user_${UUID.randomUUID().toString().take(10)}"
            val place = PlaceEntity(
                id = placeId,
                name = name.ifBlank { "${craftType.displayName} Standort" },
                category = category.ifBlank { craftType.displayName },
                craftType = craftType.name,
                siteType = siteType.name,
                isBuilding = isBuilding,
                hasUndergroundUtility = hasUndergroundUtility,
                status = "Manuell erfasst",
                lat = lat,
                lon = lon,
                address = address.ifBlank { "Koordinaten: $lat, $lon" },
                markerShape = markerShape,
                markerColor = markerColor,
                notes = notes,
                isManual = true
            )
            placeDao.insertPlace(place)

            // Auto-categorize materials based on selected craft and site type
            val autoMaterials = MaterialCategorizer.autoCategorizePlace(
                placeId = placeId,
                craftType = craftType,
                siteType = siteType,
                customNotes = notes
            )
            materialDao.insertMaterials(autoMaterials)
            _showAddPlaceDialog.value = false
        }
    }

    fun addMaterialToPlace(
        placeId: String,
        name: String,
        metalType: MetalType,
        weightKg: Double,
        pricePerKg: Double,
        condition: String,
        notes: String
    ) {
        viewModelScope.launch {
            val total = Math.round(weightKg * pricePerKg * 100.0) / 100.0
            val item = MaterialItemEntity(
                id = "mat_user_${UUID.randomUUID().toString().take(10)}",
                placeId = placeId,
                materialName = name.ifBlank { metalType.title },
                metalType = metalType.name,
                weightKg = weightKg,
                pricePerKg = pricePerKg,
                totalValueEur = total,
                condition = condition,
                quantity = 1,
                notes = notes,
                autoDetected = false,
                detectedReason = "Manuelle Benutzererfassung"
            )
            materialDao.insertMaterial(item)
            _placeForAddingMaterial.value = null
        }
    }

    fun deletePlace(placeId: String) {
        viewModelScope.launch {
            placeDao.deletePlaceById(placeId)
            if (_selectedPlace.value?.place?.id == placeId) {
                _selectedPlace.value = null
            }
        }
    }

    fun deleteMaterial(materialId: String) {
        viewModelScope.launch {
            materialDao.deleteMaterialById(materialId)
        }
    }

    fun triggerAutoMaterialGeneration(placeId: String) {
        viewModelScope.launch {
            val current = rawPlacesFlow.stateIn(viewModelScope).value.find { it.place.id == placeId } ?: return@launch
            val craft = runCatching { CraftType.valueOf(current.place.craftType) }.getOrDefault(CraftType.OTHER)
            val site = runCatching { SiteType.valueOf(current.place.siteType) }.getOrDefault(SiteType.SONSTIGES)
            val generated = MaterialCategorizer.autoCategorizePlace(
                placeId = placeId,
                craftType = craft,
                siteType = site,
                customNotes = current.place.notes
            )
            materialDao.insertMaterials(generated)
        }
    }

    fun exportPlacesJson(): String {
        val list = filteredPlaces.value
        val sb = StringBuilder("[\n")
        list.forEachIndexed { i, p ->
            sb.append("  {\n")
            sb.append("    \"id\": \"${p.place.id}\",\n")
            sb.append("    \"name\": \"${p.place.name.replace("\"", "\\\"")}\",\n")
            sb.append("    \"category\": \"${p.place.category}\",\n")
            sb.append("    \"craftType\": \"${p.place.craftType}\",\n")
            sb.append("    \"siteType\": \"${p.place.siteType}\",\n")
            sb.append("    \"lat\": ${p.place.lat},\n")
            sb.append("    \"lon\": ${p.place.lon},\n")
            sb.append("    \"address\": \"${p.place.address.replace("\"", "\\\"")}\",\n")
            sb.append("    \"totalWeightKg\": ${p.totalWeightKg},\n")
            sb.append("    \"totalValueEur\": ${p.totalEstimatedValueEur}\n")
            sb.append("  }${if (i < list.size - 1) "," else ""}\n")
        }
        sb.append("]")
        return sb.toString()
    }
}
