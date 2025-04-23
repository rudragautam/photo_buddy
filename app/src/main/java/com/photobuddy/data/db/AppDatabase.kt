package com.photobuddy.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.MemoryCapsule
import com.photobuddy.data.db.entities.PhotoEntity

@Database(entities = [PhotoEntity::class, MemoryCapsule::class,FaceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun capsuleDao(): MemoryCapsuleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "photo_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}