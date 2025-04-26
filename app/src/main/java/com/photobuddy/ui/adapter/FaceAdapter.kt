package com.photobuddy.ui.adapter




import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.photobuddy.R
import com.photobuddy.data.db.entities.FaceWithPhotoCount
import com.photobuddy.databinding.ItemFaceBinding

class FaceAdapter : RecyclerView.Adapter<FaceAdapter.FaceViewHolder>() {

    private var faces: List<FaceWithPhotoCount> = emptyList()

    inner class FaceViewHolder(private val binding: ItemFaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FaceWithPhotoCount) {
            val requestOptions = RequestOptions()
                .transforms(CenterCrop(), RoundedCorners(56))

            val thumbnailBitmap = BitmapFactory.decodeByteArray(
                item.face.thumbnail, 0, item.face.thumbnail.size
            )

            /*Glide.with(binding.root.context)
                .load(thumbnailBitmap)
                .apply(requestOptions)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_photo)
                .into(binding.faceImage)*/

            Glide.with(binding.root.context)
                .load(thumbnailBitmap)
                .circleCrop()
                .into(binding.faceImage)

//            binding.faceTitle.text = item.face.title
            binding.photoCount.text = "${item.photoCount} Photos"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaceViewHolder {
        val binding = ItemFaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FaceViewHolder, position: Int) {
        holder.bind(faces[position])

        holder.itemView.setOnClickListener {
            // You can later open ImageActivity with filtered images for this face if needed
        }
    }

    override fun getItemCount(): Int = faces.size

    fun submitList(newFaces: List<FaceWithPhotoCount>) {
        faces = newFaces
        notifyDataSetChanged()
    }
}

