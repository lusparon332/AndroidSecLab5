package com.example.androidsecmediastoreplusexif

import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberImagePainter
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var exifData by remember { mutableStateOf("") }

    // Лаунчер для выбора изображения из MediaStore
    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
        uri?.let {
            // Чтение Exif-тегов
            val inputStream = context.contentResolver.openInputStream(it)
            inputStream?.use { stream ->
                val exifInterface = androidx.exifinterface.media.ExifInterface(stream)
                val date = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_DATETIME) ?: "Unknown Date"
                val latitude = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LATITUDE) ?: "Unknown Latitude"
                val longitude = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_GPS_LONGITUDE) ?: "Unknown Longitude"
                val make = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MAKE) ?: "Unknown Make"
                val model = exifInterface.getAttribute(androidx.exifinterface.media.ExifInterface.TAG_MODEL) ?: "Unknown Model"

                // Формирование строки с Exif-тегами
                exifData = "Дата: $date\nШирота: $latitude\nДолгота: $longitude\nУстройство: $make $model"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Просмотр изображения") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            // Кнопка выбора изображения
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выбрать изображение")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение выбранного изображения
            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Отображение Exif-тегов
                Text(exifData, style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка для перехода к редактору Exif-тегов
                Button(
                    onClick = {
                        val encodedUri = URLEncoder.encode(uri.toString(), StandardCharsets.UTF_8.toString())
                        navController.navigate("editor/$encodedUri")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Редактировать Exif теги")
                }
            }
        }
    }
}