package com.example.imagetocalories.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMealsByUserId(userId: Long): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealEntity>> // Flow kullanıyoruz ki UI anlık güncellensin

    @Delete
    suspend fun deleteMeal(meal: MealEntity)
}