package com.photobuddy.ui

import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.photobuddy.R
import com.photobuddy.data.model.Photo
import com.photobuddy.databinding.ActivityImageBinding
import com.photobuddy.databinding.ActivityPhotoDetailsBinding

class PhotoDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoId = intent.getStringExtra("IMAGE_PATH")
        val photo = Gson().fromJson(intent.getStringExtra("IMAGE_PATH"), Photo::class.java)
        var requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(16))

        // Dynamically change height to create staggered effect
          // Random height between 400-700px
       /* Glide.with(this)
            .load(Uri.parse(photo.url))
            .apply(requestOptions)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_photo)
            .into(binding.imagePath)*/

        Glide.with(this)
            .load(Uri.parse(photo.url)) // Can be local file, URL, drawable, etc.
            .into(binding.imagePath)

    }
}