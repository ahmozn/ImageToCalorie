package com.example.imagetocalories.data.repository

import android.graphics.Bitmap
import com.example.imagetocalories.data.local.MealDao
import com.example.imagetocalories.data.local.MealEntity
import com.example.imagetocalories.data.remote.GeminiService
import javax.inject.Inject

class MealRepository @Inject constructor(
    private val mealDao: MealDao,
    private val geminiService: GeminiService
) {
    // Veritabanı işlemleri
    fun getAllMeals() = mealDao.getAllMeals()
    fun getMealsByUserId(userId: Long) = mealDao.getMealsByUserId(userId)
    suspend fun insertMeal(meal: MealEntity) = mealDao.insertMeal(meal)

    // Gemini Analizi
    suspend fun analyzeImage(bitmap: Bitmap) = geminiService.analyzeMealImage(bitmap)
}