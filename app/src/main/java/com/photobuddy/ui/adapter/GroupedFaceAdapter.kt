package com.photobuddy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.photobuddy.R
import com.photobuddy.viewmodel.PhotoWithFaceGroup

class GroupedFaceAdapter(private val groups: List<PhotoWithFaceGroup>) :
    RecyclerView.Adapter<GroupedFaceAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFaceName: TextView = view.findViewById(R.id.tvFaceName)
        val rvPhotos: RecyclerView = view.findViewById(R.id.rvPhotos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grouped_face, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.tvFaceName.text = group.faceName

        holder.rvPhotos.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = PhotoAdapterNew(group.photos)
        }
    }

    override fun getItemCount() = groups.size
}