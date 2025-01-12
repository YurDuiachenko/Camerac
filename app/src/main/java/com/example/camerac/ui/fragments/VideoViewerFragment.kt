package com.example.camerac.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.camerac.databinding.FragmentVideoViewerBinding
import java.io.File

class VideoViewerFragment : Fragment() {

    private lateinit var binding: FragmentVideoViewerBinding
    private var videoFilePath: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoViewerBinding.inflate(inflater, container, false)

        videoFilePath = arguments?.getString("videoFile")
        if (videoFilePath != null) {
            val videoFile = File(videoFilePath)
            val videoUri: Uri = Uri.fromFile(videoFile)
            binding.videoView.setVideoURI(videoUri)

            val mediaController = MediaController(context)
            mediaController.setAnchorView(binding.videoView)
            binding.videoView.setMediaController(mediaController)
            binding.videoView.start()
        }

        return binding.root
    }
}

