package com.photobuddy.ui.adapter

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
import com.photobuddy.databinding.ListItemAlbumBinding

class AlbumAdapter : RecyclerView.Adapter<AlbumAdapter.RowViewHolder>() {

    private var albums: List<AlbumWithImageCountAndRecentImage> = emptyList()

    // Group 2 albums in 1 row
    override fun getItemCount(): Int = Math.ceil(albums.size / 2.0).toInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemAlbumRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val firstIndex = position * 2
        val secondIndex = firstIndex + 1

        val album1 = albums.getOrNull(firstIndex)
        val album2 = albums.getOrNull(secondIndex)

        holder.bind(album1, album2)
    }

    inner class RowViewHolder(private val binding: ItemAlbumRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(album1: AlbumWithImageCountAndRecentImage?, album2: AlbumWithImageCountAndRecentImage?) {
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
            } else {
                binding.albumCard2.root.visibility = View.INVISIBLE
            }
        }
    }

    fun submitList(newAlbums: List<AlbumWithImageCountAndRecentImage>) {
        albums = newAlbums
        notifyDataSetChanged()
    }
}
