package com.example.imagetocalories.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetocalories.data.local.MealEntity
import com.example.imagetocalories.data.repository.MealRepository
import com.example.imagetocalories.data.remote.GeminiCalorieResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _analysisResult = MutableStateFlow<GeminiCalorieResponse?>(null)
    val analysisResult: StateFlow<GeminiCalorieResponse?> = _analysisResult

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun analyzeAndSaveImage(bitmap: Bitmap, imagePath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.analyzeImage(bitmap)

            result?.let {
                _analysisResult.value = it
                //Gemini'den veri gelince DB'ye kaydediyoruz
                repository.insertMeal(
                    MealEntity(
                        mealName = it.mealName,
                        calories = it.calories,
                        weightGram = it.weightGram,
                        imagePath = imagePath
                    )
                )
            }
            _isLoading.value = false
        }
    }

    fun resetResult() {
        _analysisResult.value = null
    }
}