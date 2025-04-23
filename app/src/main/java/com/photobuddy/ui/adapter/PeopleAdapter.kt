package com.photobuddy.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.photobuddy.R
import com.photobuddy.data.model.FaceGroupUiModel

class PeopleAdapter(
    private var items: List<FaceGroupUiModel>,
    private val onItemClick: (FaceGroupUiModel) -> Unit
) : RecyclerView.Adapter<PeopleAdapter.PeopleViewHolder>() {

    inner class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.faceImage)
        val name: TextView = view.findViewById(R.id.groupLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_face_group, parent, false)
        return PeopleViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleViewHolder, position: Int) {
        val group = items[position]
        holder.image.setImageBitmap(group.faceThumbnail)
        holder.name.text = group.groupId

        holder.itemView.setOnClickListener {
            onItemClick(group)
        }
    }

    fun updateData(newItems: List<FaceGroupUiModel>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size
}
