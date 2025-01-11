package com.example.camerac.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.camerac.R
import com.example.camerac.databinding.FragmentPhotoBinding
import com.example.camerac.ui.MainActivity
import java.io.File
import java.util.*

class PhotoFragment : Fragment() {
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    // Переменная для отслеживания активной камеры (задняя или фронтальная)
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private lateinit var binding: FragmentPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Проверка разрешений на камеру
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Устанавливаем действие для кнопки захвата
        binding.captureBtn.setOnClickListener { takePhoto() }

        // Устанавливаем действие для кнопки переключения камеры
        binding.rotateBtn.setOnClickListener { switchCamera() }

        outputDirectory = getOutputDirectory()

        binding.label.setOnClickListener {
            (activity as MainActivity).navigatePhotoToVideo()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Получаем CameraProvider
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Создаем Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
            }

            // Конфигурация для захвата изображений
            imageCapture = ImageCapture.Builder().build()

            try {
                // Отвязываем все привязки
                cameraProvider.unbindAll()

                // Привязываем Preview и захват изображений к жизненному циклу фрагмента
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Ошибка привязки камеры", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Захват фото
    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        // Создаем файл для сохранения изображения
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Настройки захвата изображения
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Запускаем захват изображения
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Ошибка захвата изображения: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Фото сохранено: ${photoFile.absolutePath}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    // Функция для переключения камеры
    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        // Перезапускаем камеру с новым CameraSelector
        startCamera()
    }

    // Проверка разрешений
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    // Получаем директорию для сохранения изображений
    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: requireContext().filesDir
    }

    companion object {
        private const val TAG = "CameraXFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}