package com.photobuddy.ui.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.photobuddy.R
import com.photobuddy.data.model.Photo
import com.photobuddy.databinding.ItemPhotoBinding
import com.photobuddy.ui.FullScreenImageActivity
import com.photobuddy.ui.ImageActivity


class PhotoAdapter : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    private var photos: List<Photo> = emptyList()

    inner class PhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {

            // Load image from local file path
            /*Glide.with(binding.root.context)
                .load(Uri.parse("file://${photo.url}")) // or just photo.url if it's already a Uri
                .centerCrop()
                .placeholder(R.drawable.ic_placeholder) // Add a placeholder
                .error(R.drawable.ic_photo) // Add an error image
                .into(binding.photoImage)*/

            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(itemView.context)
                .load(Uri.parse("file://${photo.url}"))
                .apply(requestOptions)
                .placeholder(R.drawable.ic_placeholder) // Add a placeholder
                .error(R.drawable.ic_photo) // Add an error image
                .into(binding.photoImage)

/*            Glide.with(binding.photoImage.context)
                .load(Uri.parse("file://${photo.url}"))
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        val shapeAppearanceModel = ShapeAppearanceModel.builder()
                            .setAllCornerSizes(16f)
                            .build()

                        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel).apply {
                            fillColor = ColorStateList.valueOf(Color.TRANSPARENT)
                            initializeElevationOverlay(binding.photoImage.context)
                            elevation = 8f
                        }

                        val drawable = LayerDrawable(arrayOf(shapeDrawable, resource))
                        binding.photoImage.setImageDrawable(drawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        binding.photoImage.setImageDrawable(placeholder)
                    }
                })*/

            // Show additional info if available
           /* photo.dateTaken?.let {
                binding.photoDate.text = Date(it).toString() // Format date properly in production
                binding.photoDate.visibility = View.VISIBLE
            } ?: run {
                binding.photoDate.visibility = View.GONE
            }*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
        /*(holder.itemView as MaskableFrameLayout).setOnMaskChangedListener {
                maskRect ->
            // Any custom motion to run when mask size changes
        }*/

        holder.itemView.setOnClickListener {
            holder.itemView.context.startActivity(Intent(holder.itemView.context,
                ImageActivity::class.java).putExtra("data",Gson().toJson(photos[position])))
        }
    }

    override fun getItemCount(): Int = photos.size

    fun submitList(newPhotos: List<Photo>) {
        photos = newPhotos
        notifyDataSetChanged()
    }
}