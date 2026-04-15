package com.example.imagetocalories.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.imagetocalories.ui.viewmodel.CameraViewModel

@Composable
fun ResultScreen(
    viewModel: CameraViewModel,
    onBackToCamera: () -> Unit
) {
    val result by viewModel.analysisResult.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (result != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🥗 Analiz Sonucu", fontSize = 20.sp, style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = result!!.mealName, fontSize = 24.sp, style = MaterialTheme.typography.bodyLarge)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Kalori", style = MaterialTheme.typography.labelMedium)
                            Text(text = "${result!!.calories} kcal", fontSize = 18.sp)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Gramaj", style = MaterialTheme.typography.labelMedium)
                            Text(text = "${result!!.weightGram} gr", fontSize = 18.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onBackToCamera) {
                Text("Yeni Fotoğraf Çek")
            }
        } else {
            // Eğer veri yoksa (hata veya henüz bitmediyse)
            CircularProgressIndicator()
            Text(text = "Veriler işleniyor...", modifier = Modifier.padding(top = 16.dp))
        }
    }
}