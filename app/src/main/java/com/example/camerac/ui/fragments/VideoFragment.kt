package com.example.camerac.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.example.camerac.R
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.camerac.databinding.FragmentVideoBinding
import com.example.camerac.ui.MainActivity
import java.io.File
import java.util.Locale

class VideoFragment : Fragment() {
    private lateinit var binding: FragmentVideoBinding
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var outputDirectory: File? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        outputDirectory = getOutputDirectory()

        binding.captureBtn.setOnClickListener { captureVideo() }

        binding.label.setOnClickListener {
            (activity as MainActivity).navigateVideoToPhoto()
        }

        binding.galleryBtn.setOnClickListener {
            (activity as MainActivity).navigateVideoToGallery()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraView.surfaceProvider)
            }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, videoCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Ошибка при запуске камеры", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    @SuppressLint("MissingPermission")
    private fun captureVideo() {
        val videoCapture = this.videoCapture ?: return

        val videoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".mp4"
        )

        val outputOptions = FileOutputOptions.Builder(videoFile).build()

        recording?.let {
            // Остановить запись
            it.stop()
            recording = null
            binding.captureBtn.setBackgroundResource(R.drawable.white_red_circle_button)
            binding.label.text = "Видео"
            return
        }

        recording = videoCapture.output
            .prepareRecording(requireContext(), outputOptions)
            .withAudioEnabled() // Включаем запись с аудио
            .start(ContextCompat.getMainExecutor(requireContext())) { recordEvent ->
                when (recordEvent) {
                    is VideoRecordEvent.Start -> {
                        binding.captureBtn.setBackgroundResource(R.drawable.white_red_rounded_button)
                        binding.label.text = "Запись..."
                    }
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            val msg = "Видео сохранено: ${videoFile.absolutePath}"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, msg)
                        } else {
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Ошибка при записи видео: ${recordEvent.error}")
                        }
                        binding.captureBtn.setBackgroundResource(R.drawable.white_red_circle_button)
                        binding.label.text = "Видео"
                    }
                }
            }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = requireContext().externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: requireContext().filesDir
    }

    private fun allPermissionsGranted(): Boolean {
        return REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val TAG = "VideoFragment"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    }
}
