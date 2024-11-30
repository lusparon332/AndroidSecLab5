package com.example.androidsecmediastoreplusexif

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface

@Composable
fun DisplayExifData(uri: Uri) {
    val context = LocalContext.current
    val exifData = remember(uri) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val exif = ExifInterface(inputStream!!)
        mapOf(
            "Дата создания" to (exif.getAttribute(ExifInterface.TAG_DATETIME) ?: "Не указана"),
            "Широта" to (exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "Не указана"),
            "Долгота" to (exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "Не указана"),
            "Устройство" to (exif.getAttribute(ExifInterface.TAG_MAKE) ?: "Не указано"),
            "Модель устройства" to (exif.getAttribute(ExifInterface.TAG_MODEL) ?: "Не указана")
        )
    }

    Column {
        exifData.forEach { (key, value) ->
            Text("$key: $value")
        }
    }
}