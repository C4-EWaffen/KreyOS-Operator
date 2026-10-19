package com.example.data

import com.example.domain.MaterialCategorizer

object InitialDataSeeder {

    suspend fun seedIfEmpty(placeDao: PlaceDao, materialDao: MaterialDao) {
        val samplePlaces = listOf(
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
                notes = "Großes Werksgelände mit kontinuierlicher Fertigung von Starkstromkabeln. Eigene Drahtzieherei und Recyclingtrommeln.",
                isManual = true
            ),
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
                isManual = true
            ),
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
                isManual = true
            ),
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
                isManual = true
            ),
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
                isManual = true
            ),
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
                isManual = true
            ),
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
                isManual = true
            ),
            PlaceEntity(
                id = "pl_trafo_netz",
                name = "Umspannwerk & Trassenknoten Süd",
                category = "Umspannwerk & Leitungsnetz",
                craftType = CraftType.UTILITY_POWER.name,
                siteType = SiteType.SONSTIGES.name,
                isBuilding = false,
                hasUndergroundUtility = true,
                status = "Kritische Infrastruktur",
                lat = 51.2410,
                lon = 7.1320,
                address = "Trassenweg Flur 12, 42119 Wuppertal",
                markerShape = "square",
                markerColor = "#38BDF8",
                notes = "Freifläche mit unterirdischem Kabelkanalnetz, Starkstrom-Erdtrasse und Transformatorstation mit massiven Kupferschienen.",
                isManual = true
            )
        )

        for (place in samplePlaces) {
            placeDao.insertPlace(place)
            val materials = MaterialCategorizer.autoCategorizePlace(
                placeId = place.id,
                craftType = CraftType.valueOf(place.craftType),
                siteType = SiteType.valueOf(place.siteType),
                customNotes = place.notes
            )
            materialDao.insertMaterials(materials)
        }
    }
}
