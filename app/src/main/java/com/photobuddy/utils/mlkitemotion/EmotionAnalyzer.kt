package com.photobuddy.utils.mlkitemotion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

object EmotionAnalyzer {
    fun detectEmotion(context: Context, bitmap: Bitmap, onResult: (String?) -> Unit) {
        val highAccuracyOpts = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL) // REQUIRED
            .build()

        val detector = FaceDetection.getClient(highAccuracyOpts)

        /*val detector = FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .enableTracking()
                .build()
        )*/

        val image = InputImage.fromBitmap(bitmap, 0)
        detector.process(image)
            .addOnSuccessListener { faces ->
                val face = faces.firstOrNull()
                if (face != null) {
                    val smileProb = face.smilingProbability ?: 0f
                    val leftEyeOpen = face.leftEyeOpenProbability ?: 0f
                    val rightEyeOpen = face.rightEyeOpenProbability ?: 0f

                    val emotion = when {
                        smileProb > 0.6 && leftEyeOpen > 0.5 && rightEyeOpen > 0.5 -> "Happy"
                        smileProb < 0.2 && leftEyeOpen < 0.3 -> "Sleepy"
                        smileProb < 0.3 && leftEyeOpen > 0.5 -> "Serious"
                        else -> "Neutral"
                    }
                    onResult(emotion)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }



}
