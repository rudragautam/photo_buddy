package com.photobuddy.utils.tflitehelper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.photobuddy.data.db.AppDatabase
import com.photobuddy.data.db.entities.Face
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.db.entities.PhotoFaceCrossRef
import java.io.ByteArrayOutputStream
import android.database.Cursor
import android.provider.MediaStore

object FaceDetectorUtils {

    private val realTimeOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .enableTracking()
        .build()

    private val detector = FaceDetection.getClient(realTimeOpts)

    /*suspend fun detectFacesFromBitmap(bitmap: Bitmap): List<Face> = withContext(Dispatchers.Default) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)
        return@withContext suspendCancellableCoroutine { cont ->
            detector.process(inputImage)
                .addOnSuccessListener { faces -> cont.resume(faces) {} }
                .addOnFailureListener { e -> cont.resume(emptyList()) {} }
        }
    }*/

/*    suspend fun syncFacesFromPhotos(context: Context, photoEntity: PhotoEntity, bitmap: Bitmap) {
        // Step 1: Detect faces

//        val faces = FaceDetectorUtils.detectFacesFromBitmap(bitmap)

        val image = InputImage.fromBitmap(bitmap, 0)
        val results = mutableListOf<Pair<Bitmap, String>>()

        val faces = detector.process(image).await()
        for (face in faces) {
            val faceBitmap = cropFaceFromBitmap(bitmap, face.boundingBox)

            // Step 2: Extract embedding
            FaceEmbeddingExtractor.loadModel(context.assets)
            val embedding = FaceEmbeddingExtractor.getEmbedding(faceBitmap)

            // Step 3: Compress face thumbnail
            val outputStream = ByteArrayOutputStream()
            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val thumbnail = outputStream.toByteArray()

            // Step 4: Save face to DB and map to photo
            val faceEntity = Face(
                title = "Face_${System.currentTimeMillis()}",
                thumbnail = thumbnail,
                embedding = embedding.toList(),
                photoId = photoEntity.id
            )
            val faceId = AppDatabase.getInstance(context).faceDao().insertFace(faceEntity)

            val crossRef = PhotoFaceCrossRef(photoId = photoEntity.id, faceId = faceId.toInt())
            AppDatabase.getInstance(context).faceDao().insertCrossRef(crossRef)
        }
    }*/

    fun cropFaceFromBitmap(original: Bitmap, boundingBox: Rect): Bitmap {
        val x = boundingBox.left.coerceAtLeast(0)
        val y = boundingBox.top.coerceAtLeast(0)
        val width = boundingBox.width().coerceAtMost(original.width - x)
        val height = boundingBox.height().coerceAtMost(original.height - y)

        return Bitmap.createBitmap(original, x, y, width, height)
    }


    suspend fun syncAllPhotos(context: Context, photos: List<PhotoEntity>) {
        for (photoEntity in photos) {
//            val filePath = getFilePathFromUri(context, imageUri)  // Replace with actual method to fetch the file path
//            Log.d("ImagePath", "File Path: $filePath")
            val filePath = getFilePathFromUri(context, Uri.parse(photoEntity.url))
            val bitmap = BitmapFactory.decodeFile(filePath) ?: continue
            syncSinglePhoto(context, photoEntity, bitmap)
        }
    }



    fun getFilePathFromUri(context: Context, uri: Uri): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)

        try {
            // Query the content resolver
            cursor = context.contentResolver.query(uri, projection, null, null, null)

            cursor?.let {
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                if (cursor.moveToFirst()) {
                    // Return the file path
                    return cursor.getString(columnIndex)
                }
            }
        } catch (e: Exception) {
            Log.e("getFilePathFromUri", "Error getting file path from URI: ${e.message}")
        } finally {
            cursor?.close()
        }

        return null // If no path was found
    }


    suspend fun syncSinglePhoto(context: Context, photoEntity: PhotoEntity, bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val faces = detector.process(image).await()

        FaceEmbeddingExtractor.loadModel(context)

        for (face in faces) {
            val faceBitmap = cropFaceFromBitmap(bitmap, face.boundingBox)

            val embedding = FaceEmbeddingExtractor.getEmbedding(faceBitmap)

            val outputStream = ByteArrayOutputStream()
            faceBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val thumbnail = outputStream.toByteArray()

            val faceEntity = Face(
                title = "Face_${System.currentTimeMillis()}",
                thumbnail = thumbnail,
                embedding = embedding.toList(),
                photoId = photoEntity.id
            )
            val faceId = AppDatabase.getInstance(context).faceDao().insertFace(faceEntity)

            val crossRef = PhotoFaceCrossRef(photoId = photoEntity.id, faceId = faceId.toInt())
            AppDatabase.getInstance(context).faceDao().insertCrossRef(crossRef)
        }
    }
}
