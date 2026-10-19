package com.example.data

enum class CraftType(val displayName: String, val code: String) {
    ROOFER("Dachdecker", "roofer"),
    PLUMBER("Gas & Wasser Installateure", "plumber"),
    ELECTRICIAN("Elektriker & Elektronik", "electrician"),
    CABLE_WORKS("Kabelwerke & Draht", "cable_works"),
    BRASS_WORKSHOP("Messing & CU Werkstätten", "brass_workshop"),
    FACTORY_HALL("Fabriken & Industriehallen", "factory_hall"),
    CONSTRUCTION("Baustellen", "construction"),
    UTILITY_POWER("Umspannwerk & Leitungsnetz", "utility_power"),
    OTHER("Sonstige Gewerbe", "other")
}

enum class SiteType(val displayName: String) {
    FIRMENSITZ("Firmensitz"),
    LAGERORT("Lagerort"),
    KUNDEN_BAUSTELLE_AKTIV("Kunden-Baustelle (Aktiv)"),
    KUNDEN_BAUSTELLE_PASSIV("Kunden-Baustelle (Passiv)"),
    FABRIKHALLEN("Fabrikhalle / Industrie"),
    WERKSTATT("Werkstatt"),
    SONSTIGES("Sonstiges")
}

enum class MetalType(
    val title: String,
    val defaultPriceEurPerKg: Double,
    val isPreciousOrHighValue: Boolean,
    val group: String
) {
    KUPFER_MILLBERRY("Kupfer Millberry (blank, 99.9% Cu)", 8.90, true, "Kupfer"),
    KUPFER_BERRY("Kupfer Berry (Altkupfer, Rohre)", 8.20, true, "Kupfer"),
    KUPFER_KABEL("Kupferkabel (Haushalts- & Schälkabel)", 4.30, true, "Kupfer"),
    MESSING_SCHWER("Messing Schwer / MS58 (Armaturen)", 5.25, true, "Messing"),
    MESSING_SPAENE("Messing Späne (rein)", 4.60, true, "Messing"),
    ROTGUSS_BRONZE("Rotguss & Zinnbronze (RG5)", 6.90, true, "Bronze"),
    TITANZINK("Titanzink / Bleche & Rinnen", 2.25, false, "Zink & Blei"),
    ALUMINIUM_DRAHT("Aluminium (Kabel / Profile)", 2.20, false, "Leichtmetall"),
    BLEI_WEICH("Kabelblei / Weichblei", 1.80, false, "Zink & Blei"),
    EDELSTAHL_V2A("Edelstahl V2A / V4A", 1.85, false, "Edelstahl"),
    SILBERKONTAKTE("Industriesilber / Schaltkontakte", 720.00, true, "Edelmetall"),
    LOETZINN("Lötzinn / Reinzinn (60/40)", 26.00, true, "Edelmetall"),
    HARTMETALL_WOLFRAM("Hartmetall / Wolfram Fräswerkzeuge", 29.50, true, "Sonderlegierung"),
    WERKZEUG_MASCHINEN("Werkzeuge & Maschinenbestand", 0.0, false, "Werkzeuge");

    companion object {
        fun fromTitle(title: String): MetalType {
            return entries.firstOrNull { it.title.equals(title, ignoreCase = true) } ?: KUPFER_BERRY
        }
    }
}
