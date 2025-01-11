package com.example.camerac.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.camerac.databinding.FragmentGalleryBinding
import com.example.camerac.ui.adapter.GalleryViewAdapter

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUris = listOf(
            Uri.parse("android.resource://com.example.camerac/mipmap/icon.png"),
            Uri.parse("android.resource://com.example.camerac/mipmap/icon.png"),
            Uri.parse("android.resource://com.example.camerac/mipmap/icon.png")
        )

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = GalleryViewAdapter(imageUris)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
