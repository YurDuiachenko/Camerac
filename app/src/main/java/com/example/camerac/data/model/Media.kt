package com.example.camerac.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    val id: Long,
    val uri: Uri,
    val name: String,
    val date: Long,
    val type: MediaType,
    var duration: String? = null
) : Parcelable