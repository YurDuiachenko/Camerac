package com.example.camerac.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.camerac.R
import com.example.camerac.databinding.ItemImageBinding
import java.io.File


class GalleryAdapter(
    private val mediaFiles: MutableList<File>, // Сделаем список изменяемым
    private val onItemClick: (File) -> Unit,    // Обработчик клика по элементу
    private val onItemDelete: (File) -> Unit    // Обработчик удаления элемента
) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageViewMedia)
        val textViewType: TextView = view.findViewById(R.id.textViewType)
        val deleteButton: ImageView = view.findViewById(R.id.deleteButton)  // Кнопка удаления
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media, parent, false)
        return GalleryViewHolder(view)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val mediaFile = mediaFiles[position]
        val fileType = when (mediaFile.extension.lowercase()) {
            "jpg", "png", "jpeg" -> "photo"
            "mp4", "mkv", "avi" -> "video"
            else -> "unknown"
        }
        holder.textViewType.text = fileType.uppercase()

        // Подгрузка изображения для фото
        if (fileType == "photo") {
            Glide.with(holder.itemView.context)
                .load(mediaFile)
                .into(holder.imageView)
        } else {
            // Для видео можно поставить превью или иконку
            holder.imageView.setImageResource(android.R.drawable.ic_media_play) // Используйте свой ресурс
        }

        // Устанавливаем слушатель на клик по элементу галереи
        holder.itemView.setOnClickListener { onItemClick(mediaFile) }

        // Устанавливаем слушатель на клик по кнопке удаления
        holder.deleteButton.setOnClickListener {
            // Удаляем файл из списка и уведомляем адаптер об изменении
            mediaFiles.removeAt(position)
            notifyItemRemoved(position) // Уведомление об удалении элемента
            onItemDelete(mediaFile)     // Вызываем функцию удаления (например, удаление файла с диска)
        }
    }

    override fun getItemCount() = mediaFiles.size
}


//class GalleryAdapter(
//    private val imageUris: List<Uri>,
//) : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
//
//    class GalleryViewHolder(val binding: ItemImageBinding) : RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
//        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return GalleryViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
//        // Получаем размер экрана
//        val displayMetrics = holder.itemView.context.resources.displayMetrics
//        val width = displayMetrics.widthPixels / 3  // 3 элемента в строке, делим ширину экрана
//
//        // Устанавливаем одинаковую ширину и высоту для квадратных ячеек
//        val layoutParams = holder.binding.imageItem.layoutParams
//        layoutParams.width = width
//        layoutParams.height = width
//        holder.binding.imageItem.layoutParams = layoutParams
//
//        // Загружаем изображение
//        holder.binding.imageItem.setImageURI(imageUris[position])
//    }
//
//    override fun getItemCount(): Int {
//        return imageUris.size
//    }
//}
