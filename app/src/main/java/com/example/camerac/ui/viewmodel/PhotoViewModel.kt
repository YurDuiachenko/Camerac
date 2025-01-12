package com.example.camerac.ui.viewmodel

import android.content.Context
import androidx.camera.core.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.camerac.domain.CameraManager
import java.io.File

class PhotoViewModel(context: Context) : ViewModel() {

    private val cameraManager = CameraManager(context)

    private val _photoSavedEvent = MutableLiveData<File>()
    val photoSavedEvent: LiveData<File> = _photoSavedEvent

    private val _cameraErrorEvent = MutableLiveData<String>()
    val cameraErrorEvent: LiveData<String> = _cameraErrorEvent

    fun startCamera(onPreviewCreated: (preview: Preview) -> Unit) {
        cameraManager.startCamera(onPreviewCreated) { }
    }

    fun switchCamera() {
        cameraManager.switchCamera()
    }

    fun takePhoto(outputDirectory: File) {
        cameraManager.takePhoto(outputDirectory,
            onSaved = { file -> _photoSavedEvent.value = file },
            onError = { exception -> _cameraErrorEvent.value = exception.message }
        )
    }
}
