package com.example.imagetocalories.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val mealName: String,
    val calories: Int,
    val weightGram: Int,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis()
)
