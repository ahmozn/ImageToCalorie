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

    private var lastImagePath: String? = null

    fun analyzeAndSaveImage(bitmap: Bitmap, imagePath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            lastImagePath = imagePath
            val result = repository.analyzeImage(bitmap)
            _analysisResult.value = result
            _isLoading.value = false
        }
    }

    fun confirmAndSave(userId: Long) {
        val result = _analysisResult.value
        val path = lastImagePath

        if (result != null && path != null) {
            viewModelScope.launch {
                repository.insertMeal(
                    MealEntity(
                        userId = userId,
                        mealName = result.mealName,
                        calories = result.calories,
                        weightGram = result.weightGram,
                        imagePath = path
                    )
                )
                resetResult()
            }
        }
    }

    fun resetResult() {
        _analysisResult.value = null
    }
}