package com.example.imagetocalories.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntity)

    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC")
    fun getWeightsForUser(userId: Long): Flow<List<WeightEntity>>

    @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC LIMIT 1")
    fun getLatestWeightForUser(userId: Long): Flow<WeightEntity?>

    @Delete
    suspend fun deleteWeight(weight: WeightEntity)
}