package com.photobuddy.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.photobuddy.R
import com.photobuddy.data.model.AlbumWithImageCountAndRecentImage
import com.photobuddy.databinding.ItemAlbumRowBinding
import com.photobuddy.ui.PhotoListActivity

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.RowViewHolder>() {

    private var albums: List<AlbumWithImageCountAndRecentImage> = emptyList()

    // Group 2 albums in 1 row
    override fun getItemCount(): Int = albums.size/4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemAlbumRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val firstIndex = position * 2
        val secondIndex = firstIndex + 1
        val thiredIndex = secondIndex + 1
        val forthIndex = thiredIndex + 1

        val album1 = albums.getOrNull(firstIndex)
        val album2 = albums.getOrNull(secondIndex)
        val album3 = albums.getOrNull(thiredIndex)
        val album4 = albums.getOrNull(forthIndex)

        holder.bind(album1, album2,album3,album4)
    }

    inner class RowViewHolder(private val binding: ItemAlbumRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            album1: AlbumWithImageCountAndRecentImage?,
            album2: AlbumWithImageCountAndRecentImage?,
            album3: AlbumWithImageCountAndRecentImage?,
            album4: AlbumWithImageCountAndRecentImage?
        ) {
            if (album1 != null) {
                binding.albumCard1.root.visibility = View.VISIBLE
                binding.albumCard1.albumName.text = album1.folderName
                binding.albumCard1.imageCount.text = "(${album1.imageCount})"
                Glide.with(binding.albumCard1.image.context)
                    .load(Uri.parse(album1.recentImageUrl))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(32)))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_photo)
                    .into(binding.albumCard1.image)

                // 👉 Click listener for album 1
                binding.albumCard1.root.setOnClickListener {
                    openPhotoList(album1)
                }
            } else {
                binding.albumCard1.root.visibility = View.INVISIBLE
            }

            if (album2 != null) {
                binding.albumCard2.root.visibility = View.VISIBLE
                binding.albumCard2.albumName.text = album2.folderName
                binding.albumCard2.imageCount.text = "(${album2.imageCount})"
                Glide.with(binding.albumCard2.image.context)
                    .load(Uri.parse(album2.recentImageUrl))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(32)))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_photo)
                    .into(binding.albumCard2.image)

                // 👉 Click listener for album 2
                binding.albumCard2.root.setOnClickListener {
                    openPhotoList(album2)
                }
            } else {
                binding.albumCard2.root.visibility = View.INVISIBLE
            }

            if (album3 != null) {
                binding.albumCard11.root.visibility = View.VISIBLE
                binding.albumCard11.albumName.text = album3.folderName
                binding.albumCard11.imageCount.text = "(${album3.imageCount})"
                Glide.with(binding.albumCard11.image.context)
                    .load(Uri.parse(album3.recentImageUrl))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(32)))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_photo)
                    .into(binding.albumCard11.image)

                // 👉 Click listener for album 3
                binding.albumCard11.root.setOnClickListener {
                    openPhotoList(album3)
                }
            } else {
                binding.albumCard11.root.visibility = View.INVISIBLE
            }

            if (album4 != null) {
                binding.albumCard22.root.visibility = View.VISIBLE
                binding.albumCard22.albumName.text = album4.folderName
                binding.albumCard22.imageCount.text = "(${album4.imageCount})"
                Glide.with(binding.albumCard22.image.context)
                    .load(Uri.parse(album4.recentImageUrl))
                    .apply(RequestOptions().transform(CenterCrop(), RoundedCorners(32)))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_photo)
                    .into(binding.albumCard22.image)

                // 👉 Click listener for album 4
                binding.albumCard22.root.setOnClickListener {
                    openPhotoList(album4)
                }
            } else {
                binding.albumCard22.root.visibility = View.INVISIBLE
            }
        }

        private fun openPhotoList(album: AlbumWithImageCountAndRecentImage) {
            val intent = Intent(binding.root.context, PhotoListActivity::class.java).apply {
                putExtra("PAGE", album.folderName)
            }
            binding.root.context.startActivity(intent)
        }

    }




    fun submitList(newAlbums: List<AlbumWithImageCountAndRecentImage>) {
        albums = newAlbums
        notifyDataSetChanged()
    }
}
