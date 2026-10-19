package com.example.data

import androidx.room.Embedded
import androidx.room.Relation

data class PlaceWithMaterials(
    @Embedded
    val place: PlaceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "placeId"
    )
    val materials: List<MaterialItemEntity> = emptyList()
) {
    val totalEstimatedValueEur: Double
        get() = materials.sumOf { it.totalValueEur }

    val totalWeightKg: Double
        get() = materials.sumOf { it.weightKg }

    val hasHighValueOrPreciousMetals: Boolean
        get() = materials.any { item ->
            runCatching { MetalType.valueOf(item.metalType).isPreciousOrHighValue }.getOrDefault(false)
        }
}
