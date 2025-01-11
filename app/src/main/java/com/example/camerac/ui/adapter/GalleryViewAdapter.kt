package com.example.camerac.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.camerac.databinding.ItemImageBinding

class GalleryViewAdapter(
    private val imageUris: List<Uri>,
) : RecyclerView.Adapter<GalleryViewAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.binding.imageItem.setImageURI(imageUris[position])
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }
}
