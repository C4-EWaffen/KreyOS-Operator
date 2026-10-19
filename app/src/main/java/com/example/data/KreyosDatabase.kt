package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PlaceEntity::class, MaterialItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KreyosDatabase : RoomDatabase() {
    abstract fun placeDao(): PlaceDao
    abstract fun materialDao(): MaterialDao

    companion object {
        @Volatile
        private var INSTANCE: KreyosDatabase? = null

        fun getInstance(context: Context): KreyosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KreyosDatabase::class.java,
                    "kreyos_scout.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
