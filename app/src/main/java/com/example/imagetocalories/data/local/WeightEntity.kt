package com.example.imagetocalories.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weights")
data class WeightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val weight: Float,
    val date: Long = System.currentTimeMillis()
)
