package com.photobuddy.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.photobuddy.data.model.AlbumWithImageCountAndRecentImage
import com.photobuddy.databinding.ItemLibraryBinding
import com.photobuddy.ui.PhotoListActivity

class LibraryAdapter : RecyclerView.Adapter<LibraryAdapter.RowViewHolder>() {

    private var albums: List<AlbumWithImageCountAndRecentImage> = emptyList()

    // Group 2 albums in 1 row
    override fun getItemCount(): Int = albums.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val binding = ItemLibraryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {

        val album1 = albums.getOrNull(position)

        holder.bind(album1)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PhotoListActivity::class.java).apply {
                putExtra("PAGE", albums.getOrNull(position)?.folderName)
            }
            it.context.startActivity(intent)
        }
    }

    inner class RowViewHolder(private val binding: ItemLibraryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            album1: AlbumWithImageCountAndRecentImage?
        ) {
            binding.libraryName.text = album1?.folderName
        }
    }

    fun submitList(newAlbums: List<AlbumWithImageCountAndRecentImage>) {
        albums = newAlbums
        notifyDataSetChanged()
    }
}
