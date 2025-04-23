package com.photobuddy.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.MemoryCapsule
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.db.entities.PhotoWithFaces

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos")
    suspend fun getAllPhotos(): List<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM photos")
    suspend fun deleteAll()

    @Query("SELECT * FROM photos WHERE id = :id")
    suspend fun getPhotoById(id: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE title LIKE :query")
    suspend fun searchPhotos(query: String): List<PhotoEntity>

    @Query("SELECT * FROM photos WHERE albumId = :albumId")
    suspend fun getPhotosByAlbum(albumId: Int): List<PhotoEntity>

    // Insert a face related to a photo
    @Insert
    suspend fun insertFace(faceEntity: FaceEntity)

    // Query all photos along with their faces
    @Transaction
    @Query("SELECT * FROM photos")
    suspend fun getAllPhotosWithFaces(): List<PhotoWithFaces>

    // Query all faces related to a particular photo
    @Query("SELECT * FROM faces WHERE photoId = :photoId")
    suspend fun getFacesForPhoto(photoId: String): List<FaceEntity>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updatePhoto(photoEntity: PhotoEntity)

    @Query("SELECT * FROM faces")
    suspend fun getAllFaces(): List<FaceEntity>
}

@Dao
interface MemoryCapsuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: MemoryCapsule)

    @Query("SELECT * FROM capsules")
    fun getAllCapsules(): LiveData<List<MemoryCapsule>>
}

