package com.example.imagetocalories.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val isLoggedIn: Boolean = false,

    val height: Float? = null,
    val currentWeight: Float? = null,
    val targetCalories: Int? = null,
    val birthDate: Long? = null,
    val gender: Int? = null, // 0: Male, 1: Female, 2: Secret
    val activityLevel: Int? = null, // 0-4

    val joinedDate: Long = System.currentTimeMillis()
)
