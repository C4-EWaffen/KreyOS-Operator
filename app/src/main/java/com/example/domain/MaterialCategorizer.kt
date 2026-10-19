package com.example.domain

import com.example.data.CraftType
import com.example.data.MaterialItemEntity
import com.example.data.MetalType
import com.example.data.SiteType
import com.example.data.StorageLocation
import java.util.UUID

object MaterialCategorizer {

    /**
     * Automatically identifies and predicts metal inventory, scrap, and sighted materials
     * based on craft trade, site type, OSM tags, and descriptive notes.
     */
    fun autoCategorizePlace(
        placeId: String,
        craftType: CraftType,
        siteType: SiteType,
        tagsMap: Map<String, String> = emptyMap(),
        customNotes: String = "",
        hasUndergroundUtility: Boolean = false,
        undergroundCableLengthM: Double = 0.0
    ): List<MaterialItemEntity> {
        val detected = mutableListOf<MaterialItemEntity>()
        val combinedText = "${tagsMap.values.joinToString(" ")} $customNotes".lowercase()

        when (craftType) {
            CraftType.LOST_PLACE -> {
                // Abandoned industrial sites & lost places: legacy copper transformer busbars, underground power cables, bronze machine bearings
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Stillgelegte Trafo-Kupferschienen (Millberry)",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = 480.0,
                        condition = "Stillgelegt / Altanlage",
                        reason = "Lost Place: Verlassene Transformatorstation & Schaltanlage",
                        storageLocation = StorageLocation.KELLER_FUNDAMENT.displayName,
                        quantity = 6
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupfer-Altbestand im Erdreich (10kV Altkabel)",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = if (undergroundCableLengthM > 0) undergroundCableLengthM * 5.2 else 1450.0,
                        condition = "Im Boden vergraben (Altbestand)",
                        reason = "Tiefbau-Altbestand: Stillgelegte 10kV Bleimantel-Kupferkabeltrasse",
                        storageLocation = StorageLocation.ERDREICH_TRASSE.displayName,
                        depthMeters = 1.1,
                        estimatedLengthMeters = if (undergroundCableLengthM > 0) undergroundCableLengthM else 280.0,
                        quantity = 1
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Altkupfer Hallenleitungen & Rohrsysteme",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = 360.0,
                        condition = "Demontagebedürftig / Leerstand",
                        reason = "Industriebrache: Wand- und Deckenrohre Cu Berry",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 12
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Schwere Maschinenlager (Rotguss RG5 & Bronze)",
                        metalType = MetalType.ROTGUSS_BRONZE,
                        weightKg = 210.0,
                        condition = "Altanlagen / Verschleißteile",
                        reason = "Fabrikruine: Gleitlager und Wellenbuchsen aus Zinnbronze",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 8
                    )
                )
            }

            CraftType.RAIL_CARGO -> {
                // OSINT Rail & Cargo: Freight yards, railway sidings, overhead catenary wire, ground cables
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Bahn-Oberleitungskupfer Ri100 (100mm² Volldraht)",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = 890.0,
                        condition = "Fahrleitungsinfrastruktur / Restdraht",
                        reason = "OSINT Rail: Hochwertiges Elektrolytkupfer der Bahnstromversorgung (~890g/m)",
                        storageLocation = StorageLocation.GLEISBETT_VERLADUNG.displayName,
                        quantity = 1
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Signal- & Gleiserdungskabel (Schottertrasse)",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = 540.0,
                        condition = "Im Gleisbett / Kabelschacht",
                        reason = "Bahntrasse: Kupferstränge für Gleisfreimeldung & Erdung",
                        storageLocation = StorageLocation.KABELKANAL_SCHACHT.displayName,
                        depthMeters = 0.5,
                        estimatedLengthMeters = 180.0,
                        quantity = 4
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Waggonladung Mischschrott & Buntmetall (E-Wagen)",
                        metalType = MetalType.MESSING_SCHWER,
                        weightKg = 2600.0,
                        condition = "Güterverladung / Umschlag",
                        reason = "Güterverkehr: Bereitstellung zur Bahnverladung",
                        storageLocation = StorageLocation.GLEISBETT_VERLADUNG.displayName,
                        quantity = 2
                    )
                )
            }

            CraftType.ROOFER -> {
                // Roofers have copper roof gutters, sheet metal, titanium zinc, lead flashings
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupfer-Dachrinnen & Blechverschnitt",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = if (siteType == SiteType.LAGERORT) 320.0 else 85.0,
                        condition = if (siteType.name.contains("BAUSTELLE")) "Gesichtet am Bau" else "Lagerbestand",
                        reason = "Dachdeckerbetrieb: Typischer Cu-Blechverschnitt & Altrinnen",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 14
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Titanzink Bleche & Rollen",
                        metalType = MetalType.TITANZINK,
                        weightKg = if (siteType == SiteType.LAGERORT) 450.0 else 120.0,
                        condition = "Lagerware / Verschnitt",
                        reason = "Dachdeckerbetrieb: Zinkblechverarbeitung für Eindeckung",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 8
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Walzblei / Kaminblei",
                        metalType = MetalType.BLEI_WEICH,
                        weightKg = 60.0,
                        condition = "Gebraucht / Demontage",
                        reason = "Dachdecker: Wand- und Kaminanschlüsse",
                        storageLocation = StorageLocation.CONTAINER_DEPOT.displayName,
                        quantity = 3
                    )
                )
            }

            CraftType.PLUMBER -> {
                // Plumbers have heavy brass fittings, copper pipes, gunmetal / bronze valves
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Messing-Fittinge & Sanitärarmaturen (MS58)",
                        metalType = MetalType.MESSING_SCHWER,
                        weightKg = if (siteType == SiteType.LAGERORT) 380.0 else 95.0,
                        condition = "Demontage & Restposten",
                        reason = "Sanitär & Heizung: Schwere Messingarmaturen & Gewindefittinge",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 45
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupferrohr Abschnitte & Altleitungen",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = if (siteType == SiteType.LAGERORT) 290.0 else 75.0,
                        condition = "Sauber / Gelagert",
                        reason = "Gas & Wasser: Heizungs- und Trinkwasserrohre Cu",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 28
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Rotguss Schieber & Flansche (RG5)",
                        metalType = MetalType.ROTGUSS_BRONZE,
                        weightKg = 140.0,
                        condition = "Demontage",
                        reason = "Installateur: Hochwertige Absperrschieber & Pumpenkörper",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 16
                    )
                )
            }

            CraftType.ELECTRICIAN -> {
                // Electricians have millberry copper, cable scraps, switchgear silver contacts
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupfer Millberry (Stromschienen & blanke Adern)",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = if (siteType == SiteType.LAGERORT) 240.0 else 60.0,
                        condition = "Blank / Hochwertig",
                        reason = "Elektrotechnik: Reinkupfer aus Schaltschränken & Starkstrom",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 10
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupferkabel Reste (Mantel- & Haushaltskabel)",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = if (siteType == SiteType.LAGERORT) 550.0 else 160.0,
                        condition = "Verlege-Reste",
                        reason = "Elektriker: Restrollen NYM-J und Installationskabel",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 18
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Industriesilber Schaltkontakte (Schütze/Relais)",
                        metalType = MetalType.SILBERKONTAKTE,
                        weightKg = 0.8,
                        condition = "Altschütze",
                        reason = "Elektrotechnik: Ag-Ni / Ag-CdO Leistungskontakte",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 5
                    )
                )
            }

            CraftType.CABLE_WORKS -> {
                // Cable factories have high volumes of clean copper wire, cable reels, pure copper
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Reinkupfer Draht & Millberry Granulat",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = 1850.0,
                        condition = "Produktionschargen",
                        reason = "Kabelwerk: Primärrohstoff für Leiterfertigung",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 1
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupfer Schälkabel / Dickleiter",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = 3200.0,
                        condition = "Trommelreste / Spulen",
                        reason = "Kabelherstellung: Schweres Erdkabel und Starkstromleiter",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 8
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Aluminium Leiterseile & Spulen",
                        metalType = MetalType.ALUMINIUM_DRAHT,
                        weightKg = 890.0,
                        condition = "Neu / Überhang",
                        reason = "Kabelwerk: Freileitungen & Al-Kabel",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 4
                    )
                )
            }

            CraftType.BRASS_WORKSHOP -> {
                // Brass workshops and foundries have heavy brass, brass chips, bronze
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Messing Stangenabfälle & Drehstücke (MS58)",
                        metalType = MetalType.MESSING_SCHWER,
                        weightKg = 920.0,
                        condition = "Massivteile",
                        reason = "Messingwerkstatt: Zerspanungsabschnitte CuZn39Pb3",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 25
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Messing-Späne trocken (rein)",
                        metalType = MetalType.MESSING_SPAENE,
                        weightKg = 1450.0,
                        condition = "Entölt in Behältern",
                        reason = "Dreherei/Fräserei: Zerspanungsrückstände",
                        storageLocation = StorageLocation.CONTAINER_DEPOT.displayName,
                        quantity = 6
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Hartmetall Wendeplatten & Fräsköpfe",
                        metalType = MetalType.HARTMETALL_WOLFRAM,
                        weightKg = 24.0,
                        condition = "Verschleißwerkzeug",
                        reason = "Präzisionswerkstatt: Wolframcarbid Zerspanungswerkzeug",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 120
                    )
                )
            }

            CraftType.FACTORY_HALL -> {
                // Industrial halls: Vacancy / For Sale or active manufacturing
                val isVacancy = siteType == SiteType.LEERSTAND_VERKAUF || combinedText.contains("leerstand") || combinedText.contains("verkauf") || combinedText.contains("miete")
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = if (isVacancy) "Hallen-Deckenstromschienen (Kupfer Millberry)" else "Industrie-Schaltschränke & Kupferschienen",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = if (isVacancy) 520.0 else 420.0,
                        condition = if (isVacancy) "In leerstehender Halle montiert" else "In Demontage / Halle",
                        reason = "Industriehalle: Hochstrom-Stromschienenverteiler 800A-1600A",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = if (isVacancy) 8 else 4
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = if (isVacancy) "Stillgelegte Druckluft- & Messingarmaturen" else "Edelstahl Rohrleitungen & Tanks (V2A)",
                        metalType = if (isVacancy) MetalType.MESSING_SCHWER else MetalType.EDELSTAHL_V2A,
                        weightKg = if (isVacancy) 260.0 else 1200.0,
                        condition = "Anlagenbestand",
                        reason = "Fabrikanlage: Verteiler und Leitungsnetze",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 15
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Elektromotoren Kupferwicklungen",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = 310.0,
                        condition = "Antriebstechnik",
                        reason = "Fabrikhalle: Statorwicklungen & Generatoren",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 6
                    )
                )
            }

            CraftType.CONSTRUCTION -> {
                // Construction sites: distinction between active and passive
                val isActive = siteType == SiteType.KUNDEN_BAUSTELLE_AKTIV
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = if (isActive) "Bau-Installationsreste (Kupferrohr & Kabel)" else "Gelagertes Altkupfer (passiv)",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = if (isActive) 140.0 else 260.0,
                        condition = if (isActive) "Laufende Arbeiten" else "Zwischengelagert",
                        reason = "Baustelle: Sanitär- und Elektroinstallationen",
                        storageLocation = if (isActive) StorageLocation.OBERIRDISCH_HALLE.displayName else StorageLocation.CONTAINER_DEPOT.displayName,
                        quantity = 5
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Messing Armaturen & Mischventile",
                        metalType = MetalType.MESSING_SCHWER,
                        weightKg = 65.0,
                        condition = if (isActive) "Montagebereit" else "Demontiert",
                        reason = "Baustelle: Heizungsverteilung",
                        storageLocation = StorageLocation.CONTAINER_DEPOT.displayName,
                        quantity = 12
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Baustellen-Werkzeuge & Elektrowerkzeuge",
                        metalType = MetalType.WERKZEUG_MASCHINEN,
                        weightKg = 80.0,
                        unitPrice = 0.0,
                        totalPrice = if (isActive) 3500.0 else 1800.0,
                        condition = if (isActive) "Aktiv im Einsatz" else "Im Baucontainer gesichert",
                        reason = "Baustellen-Ausstattung: Kernbohrer, Presszangen, Hilti",
                        storageLocation = StorageLocation.CONTAINER_DEPOT.displayName,
                        quantity = 7
                    )
                )
            }

            CraftType.UTILITY_POWER -> {
                // Substations, underground cabling, transformers
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Trafo-Kupferwicklungen & Stromschienen",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = 780.0,
                        condition = "Hochspannungsnetz",
                        reason = "Umspannwerk / Netzknotenpunkt: Reinstkupfer",
                        storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                        quantity = 3
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Erdkabel Schälkupfer (Bodenleitungen)",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = if (undergroundCableLengthM > 0) undergroundCableLengthM * 5.2 else 1100.0,
                        condition = "Infrastruktur / Erdreich",
                        reason = "Bodenleitungsnetz: Starkstrom-Trasse",
                        storageLocation = StorageLocation.ERDREICH_TRASSE.displayName,
                        depthMeters = 1.0,
                        estimatedLengthMeters = if (undergroundCableLengthM > 0) undergroundCableLengthM else 210.0,
                        quantity = 1
                    )
                )
            }

            CraftType.OTHER -> {
                if (combinedText.contains("kupfer") || combinedText.contains("copper")) {
                    detected.add(
                        createItem(
                            placeId = placeId,
                            name = "Gesichtetes Kupfermaterial",
                            metalType = MetalType.KUPFER_BERRY,
                            weightKg = 100.0,
                            condition = "Sichtung",
                            reason = "Keyword-Treffer in Objektbeschreibung",
                            storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                            quantity = 2
                        )
                    )
                }
                if (combinedText.contains("messing") || combinedText.contains("brass")) {
                    detected.add(
                        createItem(
                            placeId = placeId,
                            name = "Gesichtetes Messingmaterial",
                            metalType = MetalType.MESSING_SCHWER,
                            weightKg = 120.0,
                            condition = "Sichtung",
                            reason = "Keyword-Treffer in Objektbeschreibung",
                            storageLocation = StorageLocation.OBERIRDISCH_HALLE.displayName,
                            quantity = 10
                        )
                    )
                }
            }
        }

        // Additional detection if place has explicit underground utility or cable length
        if (hasUndergroundUtility && detected.none { it.storageLocation == StorageLocation.ERDREICH_TRASSE.displayName }) {
            val length = if (undergroundCableLengthM > 0) undergroundCableLengthM else 150.0
            val weight = Math.round(length * 5.2 * 10.0) / 10.0
            detected.add(
                createItem(
                    placeId = placeId,
                    name = "Kupfer-Altbestand im Erdreich (Versorgungs-Erdkabel)",
                    metalType = MetalType.KUPFER_KABEL,
                    weightKg = weight,
                    condition = "Im Boden verlegt",
                    reason = "Bodenleitungsnetz: Erkannte unterirdische Starkstromleitung",
                    storageLocation = StorageLocation.ERDREICH_TRASSE.displayName,
                    depthMeters = 0.9,
                    estimatedLengthMeters = length,
                    quantity = 1
                )
            )
        }

        return detected
    }

    private fun createItem(
        placeId: String,
        name: String,
        metalType: MetalType,
        weightKg: Double,
        condition: String,
        reason: String,
        unitPrice: Double = metalType.defaultPriceEurPerKg,
        totalPrice: Double? = null,
        storageLocation: String = StorageLocation.OBERIRDISCH_HALLE.displayName,
        depthMeters: Double = 0.0,
        estimatedLengthMeters: Double = 0.0,
        quantity: Int = 1
    ): MaterialItemEntity {
        val calculatedTotal = totalPrice ?: (weightKg * unitPrice)
        return MaterialItemEntity(
            id = "mat_${UUID.randomUUID().toString().take(12)}",
            placeId = placeId,
            materialName = name,
            metalType = metalType.name,
            weightKg = weightKg,
            pricePerKg = unitPrice,
            totalValueEur = Math.round(calculatedTotal * 100.0) / 100.0,
            condition = condition,
            quantity = quantity.coerceAtLeast(1),
            storageLocation = storageLocation,
            depthMeters = depthMeters,
            estimatedLengthMeters = estimatedLengthMeters,
            notes = "",
            autoDetected = true,
            detectedReason = reason
        )
    }

    /**
     * Calculates copper weight and estimated market value for underground cables
     * Standard 3x185mm² Cu underground cable yields ~5.2 kg pure copper per meter.
     */
    fun calculateUndergroundCopper(
        lengthMeters: Double,
        copperKgPerMeter: Double = 5.2
    ): Pair<Double, Double> {
        val weightKg = Math.round(lengthMeters * copperKgPerMeter * 10.0) / 10.0
        val valueEur = Math.round(weightKg * MetalType.KUPFER_KABEL.defaultPriceEurPerKg * 100.0) / 100.0
        return Pair(weightKg, valueEur)
    }
}
