package com.example.imagetocalories.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.imagetocalories.data.local.MealEntity
import com.example.imagetocalories.ui.viewmodel.AuthViewModel
import com.example.imagetocalories.ui.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState(initial = null)
    
    LaunchedEffect(currentUser) {
        currentUser?.id?.let { viewModel.setUserId(it) }
    }

    val todaysTotalCalories by viewModel.todaysTotalCalories.collectAsState(initial = 0)
    val todaysMeals by viewModel.todaysMeals.collectAsState(initial = emptyList())
    val targetCalories = currentUser?.targetCalories ?: 2000 // Default if not set

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Günlük Kalori Takibi",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "$todaysTotalCalories / $targetCalories kcal",
            style = MaterialTheme.typography.bodyLarge,
            color = if (todaysTotalCalories > targetCalories) Color.Red else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        CalorieProgressBar(
            current = todaysTotalCalories,
            target = targetCalories
        )
        
        if (todaysTotalCalories > targetCalories) {
            Text(
                text = "Hedefinizi ${todaysTotalCalories - targetCalories} kcal aştınız!",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bugün Tükettikleriniz",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        if (todaysMeals.isEmpty()) {
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(
                    text = "Henüz bir öğün eklemediniz.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(todaysMeals) { meal ->
                    MealItem(meal)
                }
            }
        }
    }
}

@Composable
fun MealItem(meal: MealEntity) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                if (!meal.imagePath.isNullOrEmpty()) {
                    AsyncImage(
                        model = meal.imagePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = rememberVectorPainter(Icons.Default.Fastfood)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Fastfood,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.mealName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = timeFormatter.format(Date(meal.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.calories} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${meal.weightGram} g",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun CalorieProgressBar(current: Int, target: Int) {
    val progress = if (target > 0) current.toFloat() / target else 0f
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
    ) {
        // Green bar for progress up to 100%
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceAtMost(1f))
                .fillMaxHeight()
                .background(if (current > target) Color.Green.copy(alpha = 0.7f) else Color.Green)
        )
        
        // Red bar for excess (right to left)
        if (current > target) {
            val excessProgress = ((current - target).toFloat() / target).coerceAtMost(1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(excessProgress)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .background(Color.Red)
            )
        }
    }
}
