package com.example.imagetocalories.data.remote

data class GeminiCalorieResponse(
    val mealName: String,
    val calories: Int,
    val weightGram: Int,
    val confidence: Double
)