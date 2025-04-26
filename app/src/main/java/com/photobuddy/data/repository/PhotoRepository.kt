package com.photobuddy.data.repository

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.provider.MediaStore
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.photobuddy.data.db.AppDatabase
import com.photobuddy.data.db.FaceDao
import com.photobuddy.data.db.PhotoDao
import com.photobuddy.data.db.entities.Face
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.db.entities.PhotoFaceCrossRef
import com.photobuddy.data.model.Photo
import com.photobuddy.data.model.Video
import com.photobuddy.utils.tflitehelper.FaceDetectorUtils
import com.photobuddy.utils.tflitehelper.FaceEmbeddingExtractor
import com.photobuddy.utils.tflitehelper.await
import com.photobuddy.viewmodel.PhotoWithFaceGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class PhotoRepository(
    private val photoDao: PhotoDao,
    private val context: Context
) {
    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()

    private val detector = FaceDetection.getClient(faceDetectorOptions)

    suspend fun getAllPhotos(): List<Photo> {
        return withContext(Dispatchers.IO) {
            photoDao.getAllPhotos().map { it.toPhoto() }
        }
    }

    suspend fun insertPhoto(photo: PhotoEntity) {
        photoDao.insertPhoto(photo)
    }

    suspend fun insertFace(photo: FaceEntity) {
        photoDao.insertFace(photo)
    }

    suspend fun updatePhoto(photoEntity: PhotoEntity) {
        photoDao.updatePhoto(photoEntity)
    }

    suspend fun refreshPhotos(photos: List<Photo>) {
        withContext(Dispatchers.IO) {
            photoDao.deleteAll()
            photoDao.insertAll(photos.map { PhotoEntity.fromPhoto(it) })
        }
    }

    suspend fun getGroupedFacesWithPhotos(): List<PhotoWithFaceGroup> {
        val faces = photoDao.getAllFaces()
        val photos = photoDao.getAllPhotos()

        return faces.map { face ->
            val matchedPhotos = photos.filter { it.faceId == face.id }
            PhotoWithFaceGroup(
                faceName = face.faceName,
                faceId = face.id,
                photos = matchedPhotos
            )
        }
    }


    suspend fun fetchPhotosFromDeviceStorage(): List<Photo> {
        return withContext(Dispatchers.IO) {
            val photos = mutableListOf<Photo>()
            val contentResolver: ContentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.SIZE
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val widthColumn = cursor.getColumnIndex(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT)

                while (cursor.moveToNext()) {
                    val id = cursor.getString(idColumn)
                    val name = cursor.getString(nameColumn)
                    val path = cursor.getString(dataColumn)
                    val dateTaken = cursor.getLong(dateColumn)
                    val width = if (widthColumn != -1) cursor.getInt(widthColumn) else null
                    val height = if (heightColumn != -1) cursor.getInt(heightColumn) else null

                    photos.add(
                        Photo(
                            id = id,
                            title = name,
                            url = path,
                            thumbnailUrl = path, // Using same path for thumbnail
                            albumId = "0", // Not applicable for device photos
                            width = width,
                            height = height,
                            dateTaken = dateTaken,
                            description = null
                        )
                    )
                }
            }

            photos
        }
    }

    private suspend fun fetchVideosFromDevice(): List<Video> {
        return withContext(Dispatchers.IO) {
            val videos = mutableListOf<Video>()
            val contentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_TAKEN,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT,
                MediaStore.Video.Media.DURATION
            )

            contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_TAKEN} DESC"
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    videos.add(
                        Video(
                            id = cursor.getString(0),
                            title = cursor.getString(1),
                            path = cursor.getString(2),
                            dateTaken = cursor.getLong(3),
                            size = cursor.getLong(4),
                            width = cursor.getInt(5),
                            height = cursor.getInt(6),
                            duration = cursor.getLong(7)
                        )
                    )
                }
            }

            videos
        }
    }

    suspend fun getPhotoById(id: String): Photo? {
        return withContext(Dispatchers.IO) {
            photoDao.getPhotoById(id)?.toPhoto()
        }
    }

    suspend fun searchPhotos(query: String): List<Photo> {
        return withContext(Dispatchers.IO) {
            photoDao.searchPhotos("%$query%").map { it.toPhoto() }
        }
    }

    suspend fun getPhotosByAlbum(albumId: Int): List<Photo> {
        return withContext(Dispatchers.IO) {
            photoDao.getPhotosByAlbum(albumId).map { it.toPhoto() }
        }
    }

    // Step 6: Repository Logic (example)
    suspend fun syncFacesWithPhotos(
        photoList: List<PhotoEntity>,
        detectFaces: suspend (Bitmap) -> List<Pair<Rect, List<Float>>>,
        getBitmap: suspend (photoUrl: String) -> Bitmap,
        faceDao: FaceDao
    ) {
        for (photo in photoList) {
            val bitmap = getBitmap(photo.url)
            val faces = detectFaces(bitmap)

            faces.forEachIndexed { index, (bounds, embedding) ->
                val faceBitmap = Bitmap.createBitmap(
                    bitmap,
                    bounds.left,
                    bounds.top,
                    bounds.width(),
                    bounds.height()
                )
                val stream = ByteArrayOutputStream()
                faceBitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val thumbnailBytes = stream.toByteArray()

                val face = Face(
                    title = "Face_${System.currentTimeMillis()}_$index",
                    photoId = photo.id,
                    thumbnail = thumbnailBytes,
                    embedding = embedding
                )
                val faceId = faceDao.insertFace(face).toInt()
                faceDao.insertCrossRef(PhotoFaceCrossRef(photo.id, faceId))
            }
        }
    }

}