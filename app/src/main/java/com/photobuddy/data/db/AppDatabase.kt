package com.photobuddy.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.photobuddy.data.db.entities.Face
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.FaceWithPhotoCount
import com.photobuddy.data.db.entities.FaceWithPhotos
import com.photobuddy.data.db.entities.MemoryCapsule
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.db.entities.PhotoFaceCrossRef
import java.io.ByteArrayOutputStream

@Database(entities = [
    PhotoEntity::class,
    MemoryCapsule::class,
    FaceEntity::class,
    Face::class,
    PhotoFaceCrossRef::class
], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun capsuleDao(): MemoryCapsuleDao
    abstract fun faceDao(): FaceDao

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

class Converters {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    @TypeConverter
    fun fromFloatList(value: List<Float>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toFloatList(value: String): List<Float> {
        return value.split(",").map { it.toFloat() }
    }

}
