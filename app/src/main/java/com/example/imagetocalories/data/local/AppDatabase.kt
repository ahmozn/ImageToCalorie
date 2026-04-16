package com.example.imagetocalories.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        MealEntity::class,
        UserEntity::class,
        WeightEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun userDao(): UserDao
    abstract fun weightDao(): WeightDao
}