package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {
    @Transaction
    @Query("SELECT * FROM places ORDER BY createdAt DESC")
    fun getAllPlacesWithMaterials(): Flow<List<PlaceWithMaterials>>

    @Transaction
    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    fun getPlaceWithMaterialsById(id: String): Flow<PlaceWithMaterials?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: PlaceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<PlaceEntity>)

    @Update
    suspend fun updatePlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deletePlaceById(id: String)

    @Query("DELETE FROM places")
    suspend fun clearAll()
}

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials WHERE placeId = :placeId ORDER BY totalValueEur DESC")
    fun getMaterialsForPlace(placeId: String): Flow<List<MaterialItemEntity>>

    @Query("SELECT * FROM materials ORDER BY totalValueEur DESC")
    fun getAllMaterials(): Flow<List<MaterialItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(item: MaterialItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterials(items: List<MaterialItemEntity>)

    @Update
    suspend fun updateMaterial(item: MaterialItemEntity)

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterialById(id: String)

    @Query("DELETE FROM materials WHERE placeId = :placeId")
    suspend fun deleteMaterialsForPlace(placeId: String)
}
