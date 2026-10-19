package com.example.network

import android.util.Log
import com.example.data.CraftType
import com.example.data.PlaceEntity
import com.example.data.SiteType
import com.example.domain.MaterialCategorizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OverpassApiClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(25, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) {
    private val endpoints = listOf(
        "https://overpass-api.de/api/interpreter",
        "https://overpass.kumi.systems/api/interpreter",
        "https://overpass.private.coffee/api/interpreter"
    )

    data class ScanResult(
        val places: List<PlaceEntity>,
        val materials: List<com.example.data.MaterialItemEntity>,
        val elementsFound: Int,
        val endpointUsed: String,
        val durationMs: Long
    )

    suspend fun scanCommercialAndCrafts(
        lat: Double,
        lon: Double,
        radiusMeters: Int = 3000,
        onlyBuildings: Boolean = false,
        onlyWithUndergroundUtility: Boolean = false
    ): ScanResult = withContext(Dispatchers.IO) {
        val t0 = System.currentTimeMillis()

        // Overpass query targeting industrial, craft trades (roofers, plumbers, electricians, cable works, brass workshops)
        // explicitly excluding residential housing and dining/cafes
        val query = buildOverpassQuery(
            lat = lat,
            lon = lon,
            radius = radiusMeters,
            onlyBuildings = onlyBuildings,
            onlyWithUtility = onlyWithUndergroundUtility
        )

        var lastError: Exception? = null
        for (ep in endpoints) {
            try {
                val mediaType = "application/x-www-form-urlencoded".toMediaType()
                val body = "data=${java.net.URLEncoder.encode(query, "UTF-8")}".toRequestBody(mediaType)
                val request = Request.Builder()
                    .url(ep)
                    .post(body)
                    .header("User-Agent", "KreyOS-Android/1.0 (Scout; research)")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w("Overpass", "Endpoint $ep returned ${response.code}")
                        return@use
                    }
                    val jsonStr = response.body?.string() ?: return@use
                    val root = JSONObject(jsonStr)
                    val elements = root.optJSONArray("elements") ?: return@use

                    val places = mutableListOf<PlaceEntity>()
                    val allMaterials = mutableListOf<com.example.data.MaterialItemEntity>()

                    for (i in 0 until elements.length()) {
                        val el = elements.getJSONObject(i)
                        val elLat = if (el.has("lat")) el.getDouble("lat") else el.optJSONObject("center")?.optDouble("lat") ?: continue
                        val elLon = if (el.has("lon")) el.getDouble("lon") else el.optJSONObject("center")?.optDouble("lon") ?: continue
                        val tags = el.optJSONObject("tags") ?: JSONObject()

                        val parsedPlace = parseElementToPlace(el, elLat, elLon, tags)
                        if (parsedPlace != null) {
                            // Filter rule: only buildings
                            if (onlyBuildings && !parsedPlace.isBuilding) continue
                            // Filter rule: if not a building, only keep if underground utility / power lines are present
                            if (onlyWithUndergroundUtility && !parsedPlace.hasUndergroundUtility) continue

                            places.add(parsedPlace)
                            val materials = MaterialCategorizer.autoCategorizePlace(
                                placeId = parsedPlace.id,
                                craftType = CraftType.valueOf(parsedPlace.craftType),
                                siteType = SiteType.valueOf(parsedPlace.siteType),
                                customNotes = parsedPlace.notes
                            )
                            allMaterials.addAll(materials)
                        }
                    }

                    return@withContext ScanResult(
                        places = places,
                        materials = allMaterials,
                        elementsFound = elements.length(),
                        endpointUsed = ep.split("/")[2],
                        durationMs = System.currentTimeMillis() - t0
                    )
                }
            } catch (e: Exception) {
                lastError = e
                Log.w("Overpass", "Failed endpoint $ep: ${e.message}")
            }
        }

        throw lastError ?: RuntimeException("Alle Overpass-Endpunkte nicht erreichbar")
    }

    private fun parseElementToPlace(
        el: JSONObject,
        lat: Double,
        lon: Double,
        tags: JSONObject
    ): PlaceEntity? {
        val tagsMap = mutableMapOf<String, String>()
        val keys = tags.keys()
        while (keys.hasNext()) {
            val k = keys.next()
            tagsMap[k.lowercase()] = tags.getString(k)
        }

        // EXCLUSION FILTER: Residential buildings and food/drink locations
        val building = tagsMap["building"] ?: ""
        val landuse = tagsMap["landuse"] ?: ""
        val amenity = tagsMap["amenity"] ?: ""

        val isResidential = building in listOf("residential", "apartments", "house", "detached", "terrace", "semidetached_house") ||
                landuse == "residential"
        val isFoodDrink = amenity in listOf("restaurant", "cafe", "fast_food", "bar", "pub", "biergarten")

        if (isResidential || isFoodDrink) {
            return null // Strictly excluded!
        }

        val craft = tagsMap["craft"] ?: ""
        val industrial = tagsMap["industrial"] ?: ""
        val manMade = tagsMap["man_made"] ?: ""
        val power = tagsMap["power"] ?: ""
        val utility = tagsMap["utility"] ?: ""
        val product = tagsMap["product"] ?: ""

        // Classify Craft & Site Type
        val craftType: CraftType
        val siteType: SiteType
        var category: String
        var markerColor = "#00D9FF"
        var markerShape = "circle"

        when {
            craft in listOf("roofer", "carpenter") -> {
                craftType = CraftType.ROOFER
                siteType = if (tagsMap.containsKey("depot") || tagsMap.containsKey("storage")) SiteType.LAGERORT else SiteType.FIRMENSITZ
                category = "Dachdeckerbetrieb"
                markerColor = "#D97706"
                markerShape = "circle"
            }
            craft in listOf("plumber", "hvac") -> {
                craftType = CraftType.PLUMBER
                siteType = if (tagsMap.containsKey("depot") || tagsMap.containsKey("storage")) SiteType.LAGERORT else SiteType.FIRMENSITZ
                category = "Gas & Wasser Installateure"
                markerColor = "#00D9FF"
                markerShape = "circle"
            }
            craft in listOf("electrician", "electronics") -> {
                craftType = CraftType.ELECTRICIAN
                siteType = if (tagsMap.containsKey("depot")) SiteType.LAGERORT else SiteType.FIRMENSITZ
                category = "Elektriker & Elektronik"
                markerColor = "#FACC15"
                markerShape = "square"
            }
            product.contains("cable") || industrial.contains("cable") || tagsMap.values.any { it.contains("kabel") } -> {
                craftType = CraftType.CABLE_WORKS
                siteType = SiteType.FABRIKHALLEN
                category = "Kabelwerke & Draht"
                markerColor = "#F97316"
                markerShape = "square"
            }
            craft in listOf("metal_construction", "blacksmith", "foundry", "locksmith", "tinsmith") ||
                    tagsMap.values.any { it.contains("messing") || it.contains("gießerei") || it.contains("dreherei") } -> {
                craftType = CraftType.BRASS_WORKSHOP
                siteType = SiteType.WERKSTATT
                category = "Messing & CU Werkstatt"
                markerColor = "#EAB308"
                markerShape = "triangle"
            }
            landuse == "construction" || building == "construction" || tagsMap.containsKey("construction") -> {
                craftType = CraftType.CONSTRUCTION
                val isActive = !tagsMap.containsKey("abandoned") && !tagsMap.containsKey("disused")
                siteType = if (isActive) SiteType.KUNDEN_BAUSTELLE_AKTIV else SiteType.KUNDEN_BAUSTELLE_PASSIV
                category = if (isActive) "Baustelle (Aktiv)" else "Baustelle (Passiv)"
                markerColor = if (isActive) "#22C55E" else "#F59E0B"
                markerShape = "triangle"
            }
            power in listOf("substation", "transformer") || utility.isNotEmpty() -> {
                craftType = CraftType.UTILITY_POWER
                siteType = SiteType.SONSTIGES
                category = "Umspannwerk & Leitungsnetz"
                markerColor = "#38BDF8"
                markerShape = "square"
            }
            industrial.isNotEmpty() || manMade in listOf("works", "factory") || building in listOf("industrial", "warehouse", "manufacture") -> {
                craftType = CraftType.FACTORY_HALL
                siteType = if (building == "warehouse") SiteType.LAGERORT else SiteType.FABRIKHALLEN
                category = "Fabrikhalle / Industrie"
                markerColor = "#EF4444"
                markerShape = "square"
            }
            else -> {
                craftType = CraftType.OTHER
                siteType = SiteType.SONSTIGES
                category = "Gewerbebetrieb"
                markerColor = "#94A3B8"
                markerShape = "circle"
            }
        }

        val hasUndergroundUtility = power.isNotEmpty() || utility.isNotEmpty() || tagsMap.containsKey("pipeline") || tagsMap.containsKey("cables")
        val isBuildingObj = building.isNotEmpty() && building != "no"

        val osmId = "${el.optString("type", "node")}/${el.optLong("id")}"
        val name = tagsMap["name"] ?: tagsMap["description"] ?: "${category} #${el.optLong("id")}"

        // Build address string
        val street = tagsMap["addr:street"] ?: ""
        val houseNum = tagsMap["addr:housenumber"] ?: ""
        val city = tagsMap["addr:city"] ?: tagsMap["addr:postcode"] ?: ""
        val address = if (street.isNotEmpty()) "$street $houseNum, $city".trim() else "OSM: $lat, $lon"

        return PlaceEntity(
            id = osmId,
            name = name,
            category = category,
            craftType = craftType.name,
            siteType = siteType.name,
            isBuilding = isBuildingObj,
            hasUndergroundUtility = hasUndergroundUtility,
            status = tagsMap["status"] ?: tagsMap["historic"] ?: "Gewerblich erfasst",
            lat = lat,
            lon = lon,
            address = address,
            osmType = el.optString("type", "node"),
            source = "https://www.openstreetmap.org/${el.optString("type")}/${el.optLong("id")}",
            tagsJson = tags.toString(),
            markerShape = markerShape,
            markerColor = markerColor,
            notes = tagsMap["note"] ?: tagsMap["description"] ?: "",
            isManual = false
        )
    }

    private fun buildOverpassQuery(
        lat: Double,
        lon: Double,
        radius: Int,
        onlyBuildings: Boolean,
        onlyWithUtility: Boolean
    ): String {
        return """
            [out:json][timeout:35];
            (
              nwr["craft"~"roofer|plumber|electrician|metal_construction|blacksmith|foundry|locksmith|carpenter|electronics|tinsmith"](around:$radius,$lat,$lon);
              nwr["industrial"~"factory|works|cables|warehouse|foundry"](around:$radius,$lat,$lon);
              nwr["man_made"~"works|water_tower|substation"](around:$radius,$lat,$lon);
              nwr["building"~"industrial|warehouse|manufacture"](around:$radius,$lat,$lon);
              nwr["landuse"="construction"](around:$radius,$lat,$lon);
              nwr["building"="construction"](around:$radius,$lat,$lon);
              nwr["power"="substation"](around:$radius,$lat,$lon);
            );
            out center tags;
        """.trimIndent()
    }
}
