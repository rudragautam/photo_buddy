package com.photobuddy.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.photobuddy.R
import com.photobuddy.data.db.entities.PhotoEntity


// PhotoAdapter.kt
class PhotoAdapterNew(private val photos: List<PhotoEntity>) :
    RecyclerView.Adapter<PhotoAdapterNew.PhotoViewHolder>() {

    inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.photoImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = photos[position]
//        Glide.with(holder.imageView.context).load(photo.url).into(holder.imageView)

        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(holder.imageView.context)
            .load(Uri.parse(photo.url))
            .apply(requestOptions)
            .placeholder(R.drawable.ic_placeholder) // Add a placeholder
            .error(R.drawable.ic_photo) // Add an error image
            .into(holder.imageView)
    }

    override fun getItemCount() = photos.size
}