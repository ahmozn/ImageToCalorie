package com.example.imagetocalories.ui.screens

import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.imagetocalories.ui.viewmodel.CameraViewModel

@Composable
fun CameraScreen(viewModel: CameraViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val result by viewModel.analysisResult.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
            bindToLifecycle(lifecycleOwner)
        }
    }

    if(result == null){
        Box(modifier = Modifier.fillMaxSize()) {
            // Kamera Preview
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        controller = cameraController
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Fotoğraf Çekme Butonu
            Button(
                onClick = {
                    takePhoto(context, cameraController) { bitmap, path ->
                        viewModel.analyzeAndSaveImage(bitmap, path)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(24.dp)
            ){
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Fotoğraf Çek"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Yemeği Analiz Et")
            }

            // Yükleniyor Spinner'ı
            val isLoading by viewModel.isLoading.collectAsState()
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }else {
        ResultScreen(viewModel=viewModel) {
            viewModel.resetResult()
        }
    }

}

private fun takePhoto(
    context: android.content.Context,
    controller: LifecycleCameraController,
    onPhotoCaptured: (android.graphics.Bitmap, String) -> Unit
) {
    controller.takePicture(
        androidx.core.content.ContextCompat.getMainExecutor(context),
        object : androidx.camera.core.ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: androidx.camera.core.ImageProxy) {
                super.onCaptureSuccess(image)

                // 1. ImageProxy -> Bitmap çevrimi
                val bitmap = image.toBitmap()

                // 2. Geçici bir dosya yolu oluştur (DB için lazım)
                val path = "${context.cacheDir}/meal_${System.currentTimeMillis()}.jpg"

                // TODO: Bitmap'i bu path'e kaydetme kodu buraya gelebilir (opsiyonel)

                onPhotoCaptured(bitmap, path)
                image.close() // Bellek sızıntısı olmasın diye kapatıyoruz
            }

            override fun onError(exception: androidx.camera.core.ImageCaptureException) {
                exception.printStackTrace()
            }
        }
    )
}