package com.example.data

import com.example.domain.MaterialCategorizer

object InitialDataSeeder {

    suspend fun seedIfEmpty(placeDao: PlaceDao, materialDao: MaterialDao) {
        val samplePlaces = listOf(
            // 1. Lost Place: Ehemaliges Walzwerk & Kabelwerk (stillgelegt, Altkupfer & Erdtrasse)
            PlaceEntity(
                id = "pl_lost_place_kabelmetall",
                name = "Ehemaliges Walzwerk Bergland (Lost Place / Stillgelegt)",
                category = "Lost Place & Industriebrache",
                craftType = CraftType.LOST_PLACE.name,
                siteType = SiteType.LOST_PLACE_INDUSTRIE.name,
                isBuilding = true,
                hasUndergroundUtility = true,
                status = "Stillgelegt seit 2018 (Lost Place)",
                lat = 51.2615,
                lon = 7.1310,
                address = "Alte Walzwerksstraße 5, 42117 Wuppertal",
                markerShape = "square",
                markerColor = "#EF4444",
                notes = "Stillgelegtes Walzwerk & Zieherei. Hallen seit Jahren unberührt. Verlassene Transformatorenstation mit massiven Kupferschienen und ausgedehntem unterirdischem 10kV Altkabel-Netz im Erdreich.",
                isManual = true,
                vacancyStatus = "Lost Place / Stillgelegt",
                hasRailAccess = true,
                railNotes = "Stillgelegtes Anschlussgleis mit ehem. Schrott-Laderampe",
                undergroundCableLengthM = 420.0,
                undergroundCableType = "10kV Bleimantel-Altkabel (3x185mm² Cu)"
            ),

            // 2. Leerstand: Industriehalle zum Verkauf / Vermietung
            PlaceEntity(
                id = "pl_leerstand_gewerbehalle",
                name = "Gewerbehalle Talbrücke (Leerstand – Zum Verkauf & Miete)",
                category = "Gewerbehalle / Leerstand",
                craftType = CraftType.FACTORY_HALL.name,
                siteType = SiteType.LEERSTAND_VERKAUF.name,
                isBuilding = true,
                hasUndergroundUtility = true,
                status = "Aktuell nicht in Betrieb – Exposé zur Vermietung / Kauf",
                lat = 51.2670,
                lon = 7.1850,
                address = "Talbrückenstraße 89, 42287 Wuppertal",
                markerShape = "square",
                markerColor = "#F59E0B",
                notes = "3.800 m² Produktionshalle. Zur Zeit geräumt und über Industrie-Gewerbemakler angeboten. Hallen-Deckenstromschienen (Kupfer Millberry 99.9%) und intakte Druckluft-Messingverteiler noch montiert.",
                isManual = true,
                vacancyStatus = "Leerstand (Zu Vermieten & Kauf)",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 180.0,
                undergroundCableType = "Boden-Versorgungsstränge 1kV 4x95mm² Cu"
            ),

            // 3. OSINT Rail & Cargo: Güterbahnhof & Schrottumschlag
            PlaceEntity(
                id = "pl_rail_cargo_langerfeld",
                name = "OSINT Rail Cargo: Güterbahnhof & Schrottumschlag Langerfeld",
                category = "OSINT Rail & Güterverkehr",
                craftType = CraftType.RAIL_CARGO.name,
                siteType = SiteType.GUETERBAHNHOF_GLEISANSCHLUSS.name,
                isBuilding = false,
                hasUndergroundUtility = true,
                status = "Aktiver Güterverkehr / Umschlagstelle",
                lat = 51.2720,
                lon = 7.2340,
                address = "Güterbahnhofstraße 1, 42389 Wuppertal-Langerfeld",
                markerShape = "triangle",
                markerColor = "#0284C7",
                notes = "Aufklärung Güterverkehr: Regelmäßiger Umschlag von Metallschrotten in offenen Eaos-Güterwaggons. Vollständig überspannt mit 15kV Oberleitung (Kupfer Ri100). Erdungskabeltrassen entlang der Schotterbett-Kabelkanäle.",
                isManual = true,
                vacancyStatus = "Aktiv / Logistik-Hub",
                hasRailAccess = true,
                railNotes = "6 Rangier- und Ladegleise, täglicher Schrott- und Mischgutverkehr",
                undergroundCableLengthM = 850.0,
                undergroundCableType = "Signal- und Bahnerdungskabel (Cu) im Gleisbett"
            ),

            // 4. Industriegleisanschluss Chemiewerk & Drahtverarbeitung
            PlaceEntity(
                id = "pl_rail_gleisanschluss_werk",
                name = "Industriegleis & Ladestelle Chemie- & Drahtwerk",
                category = "Industriegleisanschluss",
                craftType = CraftType.RAIL_CARGO.name,
                siteType = SiteType.GUETERBAHNHOF_GLEISANSCHLUSS.name,
                isBuilding = false,
                hasUndergroundUtility = true,
                status = "Gleisanschluss (Teilaktiv / Werksverkehr)",
                lat = 51.2480,
                lon = 7.1290,
                address = "Werkbahnweg 12, 42119 Wuppertal",
                markerShape = "triangle",
                markerColor = "#38BDF8",
                notes = "Eigener Werksgleisanschluss mit Laderampe und Bockkran. Altkabel-Trassen und Weichenantriebe mit Kupferwicklungen.",
                isManual = true,
                vacancyStatus = "Industriegleis / Werkverkehr",
                hasRailAccess = true,
                railNotes = "Werksanschlussgleis mit Weiche zur Hauptbahnlinie",
                undergroundCableLengthM = 320.0,
                undergroundCableType = "Schottertrassen-Signalkabel"
            ),

            // 5. Kabelwerk
            PlaceEntity(
                id = "pl_kabelwerk_01",
                name = "Kabelwerk Rhein-Wupper GmbH & Co. KG",
                category = "Kabelwerk & Drahtzieherei",
                craftType = CraftType.CABLE_WORKS.name,
                siteType = SiteType.FABRIKHALLEN.name,
                isBuilding = true,
                hasUndergroundUtility = true,
                status = "Aktiv in Betrieb",
                lat = 51.2584,
                lon = 7.1420,
                address = "Industriestraße 42, 42103 Wuppertal",
                markerShape = "square",
                markerColor = "#F97316",
                notes = "Großes Werksgelände mit kontinuierlicher Fertigung von Starkstromkabeln. Eigene Drahtzieherei, Kupfergranulat und Recyclingtrommeln.",
                isManual = true,
                vacancyStatus = "Aktiv in Betrieb",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 350.0,
                undergroundCableType = "Prüffeld-Erdkabel 20kV Cu"
            ),

            // 6. Messingwerkstatt
            PlaceEntity(
                id = "pl_messing_01",
                name = "Bergische Messing- & Metallgießerei Drehtechnik",
                category = "Messing & CU Werkstatt",
                craftType = CraftType.BRASS_WORKSHOP.name,
                siteType = SiteType.WERKSTATT.name,
                isBuilding = true,
                hasUndergroundUtility = false,
                status = "Gewerbebetrieb",
                lat = 51.2640,
                lon = 7.1590,
                address = "Gießereistraße 17, 42105 Wuppertal",
                markerShape = "triangle",
                markerColor = "#EAB308",
                notes = "Spezialwerkstatt für CNC-Zerspanung von MS58 Messing und Rotguss-Formteilen. Große Spänebehälter im Innenhof.",
                isManual = true,
                vacancyStatus = "Aktiv in Betrieb",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 0.0,
                undergroundCableType = ""
            ),

            // 7. Dachdecker Lager
            PlaceEntity(
                id = "pl_dach_lager",
                name = "Bedachungen Müller & Söhne – Hauptlager & Werkstatt",
                category = "Dachdeckerbetrieb",
                craftType = CraftType.ROOFER.name,
                siteType = SiteType.LAGERORT.name,
                isBuilding = true,
                hasUndergroundUtility = false,
                status = "Zentrallager",
                lat = 51.2512,
                lon = 7.1350,
                address = "Handwerkerhof 8, 42117 Wuppertal",
                markerShape = "circle",
                markerColor = "#D97706",
                notes = "Zentrallager mit Kantbank für Kupferbleche, Rollen Titanzink und gesammelten Alt-Kupferrinnen aus Sanierungen.",
                isManual = true,
                vacancyStatus = "Aktiv in Betrieb",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 0.0,
                undergroundCableType = ""
            ),

            // 8. Sanitär Firmensitz
            PlaceEntity(
                id = "pl_sanitaer_firmensitz",
                name = "Hahn & Partner Sanitär- und Gasinstallationen",
                category = "Gas & Wasser Installateure",
                craftType = CraftType.PLUMBER.name,
                siteType = SiteType.FIRMENSITZ.name,
                isBuilding = true,
                hasUndergroundUtility = false,
                status = "Firmensitz mit Werkstatt",
                lat = 51.2701,
                lon = 7.1680,
                address = "Kanalstraße 102, 42285 Wuppertal",
                markerShape = "circle",
                markerColor = "#00D9FF",
                notes = "Fachbetrieb für Großinstallationen. Hohes Aufkommen an Messingventilen, Rotguss und Alt-Kupferrohren aus Kesseltausch.",
                isManual = true,
                vacancyStatus = "Aktiv in Betrieb",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 0.0,
                undergroundCableType = ""
            ),

            // 9. Elektro Lager
            PlaceEntity(
                id = "pl_elektro_lager",
                name = "Elektrotechnik VoltMaster – Außenlager",
                category = "Elektriker & Elektronik",
                craftType = CraftType.ELECTRICIAN.name,
                siteType = SiteType.LAGERORT.name,
                isBuilding = true,
                hasUndergroundUtility = true,
                status = "Gewerbehalle / Materiallager",
                lat = 51.2460,
                lon = 7.1550,
                address = "Gewerbepark Nord 5, 42109 Wuppertal",
                markerShape = "square",
                markerColor = "#FACC15",
                notes = "Schaltschrankbau und Großkabelverlegung. Sammellager für Altkabel, Stromschienen (Millberry) und Schütz-Silberkontakte.",
                isManual = true,
                vacancyStatus = "Aktiv in Betrieb",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 120.0,
                undergroundCableType = "Hallenversorgungs-Trasse Cu"
            ),

            // 10. Baustelle Aktiv
            PlaceEntity(
                id = "pl_baustelle_aktiv",
                name = "Kundenbaustelle: Gewerbekomplex Tal-Achse",
                category = "Baustelle",
                craftType = CraftType.CONSTRUCTION.name,
                siteType = SiteType.KUNDEN_BAUSTELLE_AKTIV.name,
                isBuilding = true,
                hasUndergroundUtility = true,
                status = "Aktiv – Rohbau-Installation",
                lat = 51.2598,
                lon = 7.1485,
                address = "Talstraße 88, 42115 Wuppertal",
                markerShape = "triangle",
                markerColor = "#22C55E",
                notes = "Aktiver Neubau von 3 Gewerbehallen. Täglich sichtbare Kabelverlegungen, Kupferstränge und Maschinenpark vor Ort.",
                isManual = true,
                vacancyStatus = "Aktive Kundenbaustelle",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 160.0,
                undergroundCableType = "Baustrom-Kabeltrasse Cu"
            ),

            // 11. Baustelle Passiv
            PlaceEntity(
                id = "pl_baustelle_passiv",
                name = "Kundenbaustelle: Altes Textilwerk Sanierung",
                category = "Baustelle",
                craftType = CraftType.CONSTRUCTION.name,
                siteType = SiteType.KUNDEN_BAUSTELLE_PASSIV.name,
                isBuilding = true,
                hasUndergroundUtility = true,
                status = "Passiv – Baustopp / Entkernung",
                lat = 51.2490,
                lon = 7.1720,
                address = "Garnbleicherweg 14, 42289 Wuppertal",
                markerShape = "triangle",
                markerColor = "#F59E0B",
                notes = "Historische Industriehalle in Sanierungspause. Bereits demontierte Kupferrohrbündel und Gusskörper in Baucontainern.",
                isManual = true,
                vacancyStatus = "Passive Baustelle (Baustopp)",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 90.0,
                undergroundCableType = "Stillgelegte Altkabel im Fundament"
            ),

            // 12. Umspannwerk & Trassenknoten (Erdreich-Altbestand)
            PlaceEntity(
                id = "pl_trafo_netz",
                name = "Umspannwerk & Trassenknoten Süd",
                category = "Umspannwerk & Leitungsnetz",
                craftType = CraftType.UTILITY_POWER.name,
                siteType = SiteType.SONSTIGES.name,
                isBuilding = false,
                hasUndergroundUtility = true,
                status = "Kritische Infrastruktur / Netzstation",
                lat = 51.2410,
                lon = 7.1320,
                address = "Trassenweg Flur 12, 42119 Wuppertal",
                markerShape = "square",
                markerColor = "#38BDF8",
                notes = "Freifläche mit unterirdischem Kabelkanalnetz, Starkstrom-Erdtrasse und Transformatorstation mit massiven Kupferschienen im Keller.",
                isManual = true,
                vacancyStatus = "Aktiv im Netzbetrieb",
                hasRailAccess = false,
                railNotes = "",
                undergroundCableLengthM = 650.0,
                undergroundCableType = "10kV Starkstrom-Erdkabeltrasse (3x185mm² Cu) in 1.1m Tiefe"
            )
        )

        for (place in samplePlaces) {
            placeDao.insertPlace(place)
            val materials = MaterialCategorizer.autoCategorizePlace(
                placeId = place.id,
                craftType = CraftType.valueOf(place.craftType),
                siteType = SiteType.valueOf(place.siteType),
                customNotes = place.notes,
                hasUndergroundUtility = place.hasUndergroundUtility,
                undergroundCableLengthM = place.undergroundCableLengthM
            )
            materialDao.insertMaterials(materials)
        }
    }
}
