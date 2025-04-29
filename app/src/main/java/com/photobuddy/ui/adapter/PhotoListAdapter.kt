package com.photobuddy.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.photobuddy.R
import com.photobuddy.data.model.Photo
import com.photobuddy.ui.PhotoDetailsActivity
import com.photobuddy.ui.PhotoListActivity

class PhotoListAdapter(
    private val items: List<Photo>, // List of image URLs
    private val glide: RequestManager
) : RecyclerView.Adapter<PhotoListAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.carouselImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo_list, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        glide.load(Uri.parse(items.get(position).url))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PhotoDetailsActivity::class.java).apply {
                putExtra("IMAGE_PATH", Gson().toJson(items.get(position)))
            }
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = items.size
}