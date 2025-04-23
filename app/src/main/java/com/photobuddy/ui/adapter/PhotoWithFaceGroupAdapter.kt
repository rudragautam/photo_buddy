package com.photobuddy.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.photobuddy.R
import com.photobuddy.data.db.entities.PhotoEntity
import com.photobuddy.viewmodel.PhotoWithFaceGroup


class PhotoWithFaceGroupAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    fun submitList(data: List<PhotoWithFaceGroup>) {
        items.clear()
        data.forEach { group ->
            items.add(group.faceName.ifEmpty { "Face ID: ${group.faceId.take(8)}" }) // Header
            items.addAll(group.photos) // Photos under header
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is String -> VIEW_TYPE_HEADER
            is PhotoEntity -> VIEW_TYPE_PHOTO
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = inflater.inflate(R.layout.item_face_header, parent, false)
                HeaderViewHolder(view)
            }
            VIEW_TYPE_PHOTO -> {
                val view = inflater.inflate(R.layout.item_face_photo, parent, false)
                PhotoViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is String -> (holder as HeaderViewHolder).bind(item)
            is PhotoEntity -> (holder as PhotoViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val faceNameText: TextView = view.findViewById(R.id.tvFaceName)
        fun bind(name: String) {
            faceNameText.text = name
        }
    }

    inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.ivPhoto)
        fun bind(photo: PhotoEntity) {


            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(imageView.context)
                .load(Uri.parse("file://${photo.url}"))
                .apply(requestOptions)
                .placeholder(R.drawable.ic_placeholder) // Add a placeholder
                .error(R.drawable.ic_photo) // Add an error image
                .into(imageView)
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_PHOTO = 1
    }
}
