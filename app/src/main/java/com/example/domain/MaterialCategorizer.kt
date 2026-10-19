package com.example.domain

import com.example.data.CraftType
import com.example.data.MaterialItemEntity
import com.example.data.MetalType
import com.example.data.SiteType
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
        customNotes: String = ""
    ): List<MaterialItemEntity> {
        val detected = mutableListOf<MaterialItemEntity>()
        val combinedText = "${tagsMap.values.joinToString(" ")} $customNotes".lowercase()

        when (craftType) {
            CraftType.ROOFER -> {
                // Roofers have copper roof gutters, sheet metal, titanium zinc, lead flashings
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupfer-Dachrinnen & Blechverschnitt",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = if (siteType == SiteType.LAGERORT) 320.0 else 85.0,
                        condition = if (siteType.name.contains("BAUSTELLE")) "Gesichtet am Bau" else "Lagerbestand",
                        reason = "Dachdeckerbetrieb: Typischer Cu-Blechverschnitt & Altrinnen"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Titanzink Bleche & Rollen",
                        metalType = MetalType.TITANZINK,
                        weightKg = if (siteType == SiteType.LAGERORT) 450.0 else 120.0,
                        condition = "Lagerware / Verschnitt",
                        reason = "Dachdeckerbetrieb: Zinkblechverarbeitung für Eindeckung"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Walzblei / Kaminblei",
                        metalType = MetalType.BLEI_WEICH,
                        weightKg = 60.0,
                        condition = "Gebraucht / Demontage",
                        reason = "Dachdecker: Wand- und Kaminanschlüsse"
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
                        reason = "Sanitär & Heizung: Schwere Messingarmaturen & Gewindefittinge"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupferrohr Abschnitte & Altleitungen",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = if (siteType == SiteType.LAGERORT) 290.0 else 75.0,
                        condition = "Sauber / Gelagert",
                        reason = "Gas & Wasser: Heizungs- und Trinkwasserrohre Cu"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Rotguss Schieber & Flansche (RG5)",
                        metalType = MetalType.ROTGUSS_BRONZE,
                        weightKg = 140.0,
                        condition = "Demontage",
                        reason = "Installateur: Hochwertige Absperrschieber & Pumpenkörper"
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
                        reason = "Elektrotechnik: Reinkupfer aus Schaltschränken & Starkstrom"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupferkabel Reste (Mantel- & Haushaltskabel)",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = if (siteType == SiteType.LAGERORT) 550.0 else 160.0,
                        condition = "Verlege-Reste",
                        reason = "Elektriker: Restrollen NYM-J und Installationskabel"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Industriesilber Schaltkontakte (Schütze/Relais)",
                        metalType = MetalType.SILBERKONTAKTE,
                        weightKg = 0.8,
                        condition = "Altschütze",
                        reason = "Elektrotechnik: Ag-Ni / Ag-CdO Leistungskontakte"
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
                        reason = "Kabelwerk: Primärrohstoff für Leiterfertigung"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Kupfer Schälkabel / Dickleiter",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = 3200.0,
                        condition = "Trommelreste / Spulen",
                        reason = "Kabelherstellung: Schweres Erdkabel und Starkstromleiter"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Aluminium Leiterseile & Spulen",
                        metalType = MetalType.ALUMINIUM_DRAHT,
                        weightKg = 890.0,
                        condition = "Neu / Überhang",
                        reason = "Kabelwerk: Freileitungen & Al-Kabel"
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
                        reason = "Messingwerkstatt: Zerspanungsabschnitte CuZn39Pb3"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Messing-Späne trocken (rein)",
                        metalType = MetalType.MESSING_SPAENE,
                        weightKg = 1450.0,
                        condition = "Entölt in Behältern",
                        reason = "Dreherei/Fräserei: Zerspanungsrückstände"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Hartmetall Wendeplatten & Fräsköpfe",
                        metalType = MetalType.HARTMETALL_WOLFRAM,
                        weightKg = 24.0,
                        condition = "Verschleißwerkzeug",
                        reason = "Präzisionswerkstatt: Wolframcarbid Zerspanungswerkzeug"
                    )
                )
            }

            CraftType.FACTORY_HALL -> {
                // Industrial halls have heavy machinery, stainless steel, transformers, motors
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Industrie-Schaltschränke & Kupferschienen",
                        metalType = MetalType.KUPFER_MILLBERRY,
                        weightKg = 420.0,
                        condition = "In Demontage / Halle",
                        reason = "Industriehalle: Hochstromverteilung"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Edelstahl Rohrleitungen & Tanks (V2A)",
                        metalType = MetalType.EDELSTAHL_V2A,
                        weightKg = 1200.0,
                        condition = "Anlagenbau",
                        reason = "Fabrikanlage: Prozessrohre & Behälter"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Elektromotoren Kupferwicklungen",
                        metalType = MetalType.KUPFER_BERRY,
                        weightKg = 310.0,
                        condition = "Antriebstechnik",
                        reason = "Fabrikhalle: Statorwicklungen & Generatoren"
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
                        reason = "Baustelle: Sanitär- und Elektroinstallationen"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Messing Armaturen & Mischventile",
                        metalType = MetalType.MESSING_SCHWER,
                        weightKg = 65.0,
                        condition = if (isActive) "Montagebereit" else "Demontiert",
                        reason = "Baustelle: Heizungsverteilung"
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
                        reason = "Baustellen-Ausstattung: Kernbohrer, Presszangen, Hilti"
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
                        reason = "Umspannwerk / Netzknotenpunkt: Reinstkupfer"
                    )
                )
                detected.add(
                    createItem(
                        placeId = placeId,
                        name = "Erdkabel Schälkupfer (Bodenleitungen)",
                        metalType = MetalType.KUPFER_KABEL,
                        weightKg = 1100.0,
                        condition = "Infrastruktur",
                        reason = "Bodenleitungsnetz: Starkstrom-Trasse"
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
                            reason = "Keyword-Treffer in Objektbeschreibung"
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
                            reason = "Keyword-Treffer in Objektbeschreibung"
                        )
                    )
                }
            }
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
        totalPrice: Double? = null
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
            quantity = 1,
            notes = "",
            autoDetected = true,
            detectedReason = reason
        )
    }
}
