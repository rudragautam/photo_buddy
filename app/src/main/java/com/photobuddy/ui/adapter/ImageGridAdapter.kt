package com.photobuddy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.photobuddy.R
import java.io.File

class ImageGridAdapter(
    private val imagePaths: List<String>
) : RecyclerView.Adapter<ImageGridAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val path = imagePaths[position]
        Glide.with(holder.itemView.context)
            .load(File(path))
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imagePaths.size
}
