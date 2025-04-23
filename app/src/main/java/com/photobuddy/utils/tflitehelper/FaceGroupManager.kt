package com.photobuddy.utils.tflitehelper


import android.content.Context
import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.math.sqrt

import com.google.android.gms.tasks.Task
import com.photobuddy.data.db.entities.FaceEntity
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.model.FaceGroup
import com.photobuddy.data.model.FaceGroupUiModel
import com.photobuddy.data.model.Photo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.nio.channels.FileChannel




class FaceGroupManager(context: Context) {

    private val faceGroups = mutableListOf<FaceGroup>()

    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .build()

    private val detector = FaceDetection.getClient(faceDetectorOptions)
    private val interpreter: Interpreter
    private val groups = mutableListOf<FaceGroup>()

    init {
        interpreter = Interpreter(loadModelFile(context))

    }

    // Detects faces in bitmap and returns their group IDs
    suspend fun processImage(bitmap: Bitmap): List<Pair<Bitmap, String>> {
        val image = InputImage.fromBitmap(bitmap, 0)
        val results = mutableListOf<Pair<Bitmap, String>>()

        val faces = detector.process(image).await()  // Extension using kotlinx-coroutines-play-services
        for (face in faces) {
            val box = face.boundingBox
            val faceBitmap = Bitmap.createBitmap(
                bitmap,
                box.left.coerceAtLeast(0),
                box.top.coerceAtLeast(0),
                box.width().coerceAtMost(bitmap.width - box.left),
                box.height().coerceAtMost(bitmap.height - box.top)
            )

            val embedding = getFaceEmbedding(faceBitmap)
            val groupId = assignToGroup(embedding)
            results.add(Pair(faceBitmap, groupId))
        }

        return results
    }

    private fun getFaceEmbedding(bitmap: Bitmap): FloatArray {
        val input = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(128) }
        interpreter.run(input, output)
        return output[0]
    }

    // This function converts FaceGroup data into UI-friendly data
    fun getFaceGroupsAsUiModel(): List<FaceGroupUiModel> {
        return faceGroups.mapIndexed { index, group ->
            FaceGroupUiModel(
                groupId = "Person ${index + 1}",
                faceThumbnail = group.faceBitmap,
                imagePaths = group.imagePaths
            )
        }
    }

    // This is where you populate your faceGroups with actual data
    fun addFaceGroup(group: FaceGroup) {
        faceGroups.add(group)
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, 160, 160, true)
        val buffer = ByteBuffer.allocateDirect(1 * 160 * 160 * 3 * 4)
        buffer.order(ByteOrder.nativeOrder())

        for (y in 0 until 160) {
            for (x in 0 until 160) {
                val pixel = resized.getPixel(x, y)
                buffer.putFloat(((pixel shr 16 and 0xFF) - 127.5f) / 128.0f)
                buffer.putFloat(((pixel shr 8 and 0xFF) - 127.5f) / 128.0f)
                buffer.putFloat(((pixel and 0xFF) - 127.5f) / 128.0f)
            }
        }

        return buffer
    }

    fun groupSimilarFaces(photos: List<PhotoEntity>): Map<FaceEntity, List<PhotoEntity>> {
        val embeddingMap = mutableMapOf<PhotoEntity, List<Float>>()

        // Step 1: Generate mock face embeddings for each photo
        photos.forEach { photo ->
            val embedding = generateMockEmbedding()
            embeddingMap[photo] = embedding
        }

        // Step 2: Cluster based on cosine similarity
        val clusters = mutableListOf<MutableList<PhotoEntity>>()

        for ((photo, embedding) in embeddingMap) {
            var matchedCluster: MutableList<PhotoEntity>? = null

            for (cluster in clusters) {
                val representative = cluster.first()
                val repEmbedding = embeddingMap[representative]!!

                if (cosineSimilarity(embedding, repEmbedding) > 0.95f) { // Similar enough
                    matchedCluster = cluster
                    break
                }
            }

            if (matchedCluster != null) {
                matchedCluster.add(photo)
            } else {
                clusters.add(mutableListOf(photo))
            }
        }

        // Step 3: Create FaceEntity for each cluster
        val result = mutableMapOf<FaceEntity, List<PhotoEntity>>()

        clusters.forEachIndexed { index, photoGroup ->
            val faceName = "Face_${index + 1}"
            val faceEntity = FaceEntity(
                id = UUID.randomUUID().toString(),
                photoId = photoGroup.first().id,
                faceName = faceName,
                faceCoordinates = null // Optional: you can extend this
            )
            result[faceEntity] = photoGroup
        }

        return result
    }


    fun generateMockEmbedding(size: Int = 128): List<Float> {
        return List(size) { (0..1000).random() / 1000f }
    }

    fun cosineSimilarity(vec1: List<Float>, vec2: List<Float>): Float {
        /*val dotProduct = a.zip(b).sumOf { it.first * it.second }
        val normA = Math.sqrt(a.sumOf { it * it }.toDouble()).toFloat()
        val normB = Math.sqrt(b.sumOf { it * it }.toDouble()).toFloat()
        return dotProduct / (normA * normB + 1e-6f)*/

        val dot = vec1.zip(vec2) { a, b -> a * b }.sum()
        val mag1 = sqrt(vec1.fold(0.0f) { acc, fl -> acc + fl * fl })
        val mag2 = sqrt(vec2.fold(0.0f) { acc, fl -> acc + fl * fl })
        return dot / (mag1 * mag2)
    }




    private fun assignToGroup(embedding: FloatArray): String {
        for (group in groups) {
            val similarity = group.representativeEmbedding?.let { cosineSimilarity(embedding, it) }
            if (similarity!! > 0.8f) return group.groupId!!
        }

        val newGroupId = UUID.randomUUID().toString()
        groups.add(FaceGroup(newGroupId, representativeEmbedding = embedding))
        return newGroupId
    }

    private fun cosineSimilarity(vec1: FloatArray, vec2: FloatArray): Float {
        val dot = vec1.zip(vec2) { a, b -> a * b }.sum()
        val mag1 = sqrt(vec1.fold(0.0f) { acc, fl -> acc + fl * fl })
        val mag2 = sqrt(vec2.fold(0.0f) { acc, fl -> acc + fl * fl })
        return dot / (mag1 * mag2)
    }


    // Bottom of FaceGroupManager.kt (outside class)

    private fun loadModelFile(context: Context): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd("facenet.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }


}

suspend fun <T> Task<T>.await(): T = suspendCancellableCoroutine { cont ->
    addOnSuccessListener { cont.resume(it) }
    addOnFailureListener { cont.resumeWithException(it) }
}
