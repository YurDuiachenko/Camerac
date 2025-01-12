package com.example.camerac.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.camerac.databinding.FragmentPhotoViewerBinding
import java.io.File

class PhotoViewerFragment : Fragment() {

    private lateinit var binding: FragmentPhotoViewerBinding
    private var photoFilePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoViewerBinding.inflate(inflater, container, false)

        photoFilePath = arguments?.getString("photoFile")
        if (photoFilePath != null) {
            val photoFile = File(photoFilePath)
            Glide.with(requireContext()).load(photoFile).into(binding.imageViewPhoto)
        }

        return binding.root
    }
}

