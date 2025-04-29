package com.photobuddy.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.photobuddy.data.db.entities.Face
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.FaceWithPhotoCount
import com.photobuddy.data.db.entities.FaceWithPhotos
import com.photobuddy.data.db.entities.MemoryCapsule
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.db.entities.PhotoFaceCrossRef
import com.photobuddy.data.db.entities.PhotoWithFaces
import com.photobuddy.data.model.AlbumWithImageCountAndRecentImage
import com.photobuddy.data.model.Photo

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

    @Query("SELECT * FROM photos WHERE faceId = :faceId")
    suspend fun getPhotosByFace(faceId: Int): List<PhotoEntity>

    @Query("UPDATE photos SET faceId = :newId WHERE faceId = :oldId")
    suspend fun updateFaceId(oldId: Int, newId: Int)

/*    @Query("""
        SELECT folderName, 
               albumId, 
               COUNT(*) AS imageCount, 
               (SELECT url FROM photos p WHERE p.albumId = photos.albumId ORDER BY p.dateTaken DESC LIMIT 1) AS recentImageUrl
        FROM photos 
        GROUP BY folderName, albumId
    """)*/
    @Query("""
    SELECT folderName, 
           albumId, 
           COUNT(*) AS imageCount, 
           (SELECT url FROM photos p WHERE p.albumId = photos.albumId ORDER BY p.dateTaken DESC LIMIT 1) AS recentImageUrl
    FROM photos 
    GROUP BY folderName, albumId
    ORDER BY imageCount DESC
""")

    suspend fun getAllAlbumsWithImageCountAndRecentImage(): List<AlbumWithImageCountAndRecentImage>

    @Query("""
        SELECT * FROM photos
        WHERE albumId = :albumId
    """)
    suspend fun getPhotosByAlbumId(albumId: Int): List<PhotoEntity>

    @Query("""
    SELECT * 
    FROM photos 
    WHERE folderName = :folderName
    ORDER BY dateTaken DESC
""")
    suspend fun getImageByFolderName(folderName: String): List<Photo>



}



@Dao
interface MemoryCapsuleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapsule(capsule: MemoryCapsule)

    @Query("SELECT * FROM capsules")
    fun getAllCapsules(): LiveData<List<MemoryCapsule>>
}

@Dao
interface FaceDao {
    @Insert
    suspend fun insertFace(face: Face): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: PhotoFaceCrossRef)

    @Transaction
    @Query("SELECT * FROM face WHERE faceId = :faceId")
    suspend fun getFaceWithPhotos(faceId: Int): FaceWithPhotos

    @Transaction
    @Query("SELECT * FROM face")
    suspend fun getAllFacesWithPhotoCount(): List<FaceWithPhotoCount>

}

