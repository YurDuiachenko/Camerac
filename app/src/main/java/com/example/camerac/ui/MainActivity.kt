package com.example.camerac.ui

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.camerac.R
import com.example.camerac.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.fragment_container) as NavHostFragment
            navController = navHostFragment.navController

    }

    fun navigatePhotoToVideo() {
        navController.navigate(R.id.action_photoFragment_to_videoFragment)
    }

    fun navigateVideoToPhoto() {
        navController.navigate(R.id.action_videoFragment_to_photoFragment)
    }

    fun navigatePhotoToGallery() {
        navController.navigate(R.id.action_photoFragment_to_galleryFragment)
    }

    fun navigateVideoToGallery() {
        navController.navigate(R.id.action_videoFragment_to_galleryFragment)
    }
}
