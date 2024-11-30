package com.example.androidsecmediastoreplusexif

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditorScreen(navController: NavController, imageUri: Uri) {
    val context = LocalContext.current

    // Copy file to internal storage
    val file = copyImageToInternalStorage(context, imageUri)
    val exif = file?.let { ExifInterface(it) }

    // States for Exif fields
    var date by remember { mutableStateOf(exif?.getAttribute(ExifInterface.TAG_DATETIME) ?: "") }
    var latitude by remember { mutableStateOf(exif?.getAttribute(ExifInterface.TAG_GPS_LATITUDE) ?: "") }
    var longitude by remember { mutableStateOf(exif?.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) ?: "") }
    var deviceMake by remember { mutableStateOf(exif?.getAttribute(ExifInterface.TAG_MAKE) ?: "") }
    var deviceModel by remember { mutableStateOf(exif?.getAttribute(ExifInterface.TAG_MODEL) ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = date, onValueChange = { date = it }, label = { Text("Дата создания") })
        TextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Широта") })
        TextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Долгота") })
        TextField(value = deviceMake, onValueChange = { deviceMake = it }, label = { Text("Устройство") })
        TextField(value = deviceModel, onValueChange = { deviceModel = it }, label = { Text("Модель устройства") })

        Spacer(modifier = Modifier.height(20.dp))
        Button(onClick = {
            // Update and save Exif data
            exif?.apply {
                setAttribute(ExifInterface.TAG_DATETIME, date)
                setAttribute(ExifInterface.TAG_GPS_LATITUDE, latitude)
                setAttribute(ExifInterface.TAG_GPS_LONGITUDE, longitude)
                setAttribute(ExifInterface.TAG_MAKE, deviceMake)
                setAttribute(ExifInterface.TAG_MODEL, deviceModel)
                saveAttributes()
            }
            navController.popBackStack() // Return to main screen
        }) {
            Text("Сохранить")
        }
    }
}

// Function to copy image to internal storage for editing
fun copyImageToInternalStorage(context: Context, uri: Uri): File? {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val file = File(context.filesDir, "temp_image.jpg")
        FileOutputStream(file).use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file
    }
}