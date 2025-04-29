package com.photobuddy.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.photobuddy.R
import com.photobuddy.data.model.Photo
import com.photobuddy.databinding.ItemPhotoBinding
import com.photobuddy.ui.PhotoListActivity

class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private var photos: List<Photo> = emptyList()

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            // Apply rounded corners and center crop
            var requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(16))

            // Dynamically change height to create staggered effect
            val layoutParams = binding.photoImage.layoutParams
            layoutParams.height = (400..700).random()  // Random height between 400-700px
            binding.photoImage.layoutParams = layoutParams

            // Load the image
            Glide.with(itemView.context)
                .load(Uri.parse(photo.url))
                .apply(requestOptions)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_photo)
                .into(binding.photoImage)

            // Image click to view fullscreen
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, PhotoListActivity::class.java).apply {
                    putExtra("data", Gson().toJson(photo))
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount(): Int = photos.size

    fun submitList(newPhotos: List<Photo>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}
