package com.photobuddy.utils.tflitehelper

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

object FaceEmbeddingExtractor {

    private lateinit var interpreter: Interpreter

    /*init {
        interpreter = Interpreter(loadModelFile(context))

    }*/

    fun loadModel(context: Context) {
        interpreter = Interpreter(loadModelFile(context))
        /*if (!::interpreter.isInitialized) {
            val fileDescriptor = assetManager.openFd(modelPath)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            interpreter = Interpreter(modelBuffer)
        }*/

    }

    private fun loadModelFile(context: Context): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd("facenet.tflite")
        val inputStream = assetFileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun getEmbedding(bitmap: Bitmap): FloatArray {
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, true)
        val input = Array(1) { Array(160) { Array(160) { FloatArray(3) } } }

        for (x in 0 until 160) {
            for (y in 0 until 160) {
                val px = scaledBitmap.getPixel(x, y)
                input[0][y][x][0] = ((px shr 16 and 0xFF) - 127.5f) / 128.0f
                input[0][y][x][1] = ((px shr 8 and 0xFF) - 127.5f) / 128.0f
                input[0][y][x][2] = ((px and 0xFF) - 127.5f) / 128.0f
            }
        }

       /* val output = Array(1) { FloatArray(128) }
        interpreter.run(input, output)*/

        val output = Array(1) { FloatArray(512) } // Make sure this matches the actual model output
        interpreter.run(input, output)
        return output[0]

//        return output[0]
    }
}
