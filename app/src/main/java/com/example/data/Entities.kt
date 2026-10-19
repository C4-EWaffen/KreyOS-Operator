package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val craftType: String, // from CraftType.name
    val siteType: String,  // from SiteType.name
    val isBuilding: Boolean,
    val hasUndergroundUtility: Boolean,
    val status: String,
    val lat: Double,
    val lon: Double,
    val address: String,
    val osmType: String = "manual",
    val source: String = "",
    val tagsJson: String = "{}",
    val markerShape: String = "circle",
    val markerColor: String = "#00D9FF",
    val notes: String = "",
    val isManual: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "materials",
    foreignKeys = [
        ForeignKey(
            entity = PlaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["placeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["placeId"])]
)
data class MaterialItemEntity(
    @PrimaryKey
    val id: String,
    val placeId: String,
    val materialName: String,
    val metalType: String, // from MetalType.name
    val weightKg: Double,
    val pricePerKg: Double,
    val totalValueEur: Double,
    val condition: String = "Gesichtet",
    val quantity: Int = 1,
    val notes: String = "",
    val autoDetected: Boolean = false,
    val detectedReason: String = ""
)
