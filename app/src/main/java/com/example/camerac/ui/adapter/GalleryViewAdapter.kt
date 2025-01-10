package com.example.musting.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musting.databinding.CurrentsItemLayoutBinding
import com.example.musting.ui.model.Currency

class GalleryViewAdapter(private val data: List<Currency>) : RecyclerView.Adapter<GalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = CurrentsItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GalleryViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = data[position]
        with(holder.binding) {
            shortName.text = item.shortName
            fullName.text = item.fullName
            cost.text = "$" + item.cost.toString()
            grow.text = when (item.grow >= 0) {
                true -> "+" + item.grow.toString() + "%"
                else -> item.grow.toString() + "%"
            }

            if (item.grow < 0) {
                grow.setTextColor(Color.parseColor("#A02A38"))
            } else {
                grow.setTextColor(Color.parseColor("#45B884"))
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
