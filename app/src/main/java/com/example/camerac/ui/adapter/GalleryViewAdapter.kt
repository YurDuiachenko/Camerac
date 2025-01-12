package com.example.camerac.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
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
        // Получаем размер экрана
        val displayMetrics = holder.itemView.context.resources.displayMetrics
        val width = displayMetrics.widthPixels / 3  // 3 элемента в строке, делим ширину экрана

        // Устанавливаем одинаковую ширину и высоту для квадратных ячеек
        val layoutParams = holder.binding.imageItem.layoutParams
        layoutParams.width = width
        layoutParams.height = width
        holder.binding.imageItem.layoutParams = layoutParams

        // Загружаем изображение
        holder.binding.imageItem.setImageURI(imageUris[position])
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }
}
