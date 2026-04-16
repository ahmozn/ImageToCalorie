package com.example.imagetocalories

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.imagetocalories.ui.MainContainer
import com.example.imagetocalories.ui.theme.ImageToCaloriesTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Launcher'ı burada tanımlıyoruz
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // İzin verildiğinde UI zaten CameraScreen içinde olduğu için
            // CameraX lifecycle'ı takip edip görüntüyü getirecektir.
        } else {
            Toast.makeText(this,"Camera permission is required to use this feature", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Uygulama her açıldığında kontrol et, yoksa iste
        checkCameraPermission()

        setContent {
            ImageToCaloriesTheme {
                var showExitToast by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                BackHandler(enabled = !showExitToast) {
                    showExitToast = true
                    Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
                    scope.launch {
                        delay(2000)
                        showExitToast = false
                    }
                }

                MainContainer()
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Zaten izin var, bi' şey yapmaya gerek yok
            }
            else -> {
                // İzni tetikle!
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}