package com.photobuddy.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.photobuddy.R
import com.photobuddy.base.BaseActivity
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.data.model.Photo
import com.photobuddy.databinding.ActivityFullScreenImageBinding
import com.photobuddy.databinding.ActivityImageBinding
import com.photobuddy.utils.mlkitemotion.EmotionAnalyzer
import com.photobuddy.viewmodel.PhotoViewModel

class ImageActivity : BaseActivity() {
    private lateinit var binding: ActivityImageBinding
    private val photoViewModel: PhotoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var dataimage=Gson().fromJson(intent.getStringExtra("data"), Photo::class.java)
//        val imageUri = Uri.parse(dataimage.url)
        val imageUri=Uri.parse("file://${dataimage.url}")
        val bitmap = getBitmapFromUri(this, imageUri)

        bitmap?.let {
            EmotionAnalyzer.detectEmotion(this, it) { emotion ->
                val photo = PhotoEntity(
                    url = imageUri.toString(),
                    dateTaken = System.currentTimeMillis(),
                    detectedEmotion = emotion,
                    id = dataimage.id,
                    title = dataimage.title,
                    thumbnailUrl = dataimage.thumbnailUrl,
                    albumId = dataimage.albumId,
                    width = dataimage.width,
                    height = dataimage.height,
                    description = dataimage.description
                )
                photoViewModel.insert(photo)
                Log.d("EmotionAnalyzer", "Detected emotion: $emotion")
            }
        }

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(this)
            .load(imageUri)
            .apply(requestOptions)
            .placeholder(R.drawable.ic_placeholder) // Add a placeholder
            .error(R.drawable.ic_photo) // Add an error image
            .into(binding.photoImage)

    }

    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
}