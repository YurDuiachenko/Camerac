// data/repository/PhotoRepository.kt
package com.example.camerac.data.repository

import android.content.Context
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.example.camerac.data.model.Photo
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PhotoRepository(private val context: Context) {

    fun takePhoto(imageCapture: ImageCapture, onSuccess: (Photo) -> Unit) {
        val photoFile = File(
            getOutputDirectory(),
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val photo = Photo(photoFile, photoFile.absolutePath)
                    onSuccess(photo)
                }

                override fun onError(exc: ImageCaptureException) {
                }
            }
        )
    }

    fun getOutputDirectory(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, "CameraApp").apply { mkdirs() }
        }
        return mediaDir ?: context.filesDir
    }
}
