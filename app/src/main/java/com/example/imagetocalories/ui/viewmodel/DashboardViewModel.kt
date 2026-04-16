package com.example.imagetocalories.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagetocalories.data.local.MealEntity
import com.example.imagetocalories.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _userId = MutableStateFlow<Long?>(null)

    fun setUserId(id: Long) {
        _userId.value = id
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val todaysMeals: Flow<List<MealEntity>> = _userId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else mealRepository.getMealsByUserId(id).map { meals ->
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = calendar.timeInMillis

            meals.filter { it.timestamp in startOfDay..endOfDay }
        }
    }

    val todaysTotalCalories: Flow<Int> = todaysMeals.map { meals ->
        meals.sumOf { it.calories }
    }
}
