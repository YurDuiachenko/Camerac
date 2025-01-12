package com.example.camerac.ui.fragments

import android.Manifest
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        // Проверка версии Android и разрешений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            loadMedia()
        } else {
            if (isPermissionGranted()) {
                loadMedia()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_STORAGE)
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadMedia() {
        val mediaUris = getMediaUris()

        // Настройка адаптера для отображения сетки с квадратными элементами
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Применяем GridLayout с квадратными ячейками
        val adapter = GalleryViewAdapter(mediaUris)
        binding.recyclerView.adapter = adapter
    }

    private fun getMediaUris(): List<Uri> {
        val mediaUris = mutableListOf<Uri>()

        // Получаем изображения
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        mediaUris.addAll(getUrisFromMediaStore(imageUri))

        // Получаем видео
        val videoUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        mediaUris.addAll(getUrisFromMediaStore(videoUri))

        return mediaUris
    }

    private fun getUrisFromMediaStore(mediaUri: Uri): List<Uri> {
        val mediaList = mutableListOf<Uri>()
        val contentResolver: ContentResolver = requireContext().contentResolver

        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val cursor = contentResolver.query(mediaUri, projection, null, null, null)

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(mediaUri, id.toString())
                mediaList.add(contentUri)
            }
        }
        return mediaList
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                loadMedia() // Разрешение предоставлено, загружаем контент
            } else {
                Toast.makeText(requireContext(), "Разрешение на доступ к галерее не предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CODE_READ_STORAGE = 101
    }
}
