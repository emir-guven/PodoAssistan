package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.StreakRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StreakDao {
    @Query("SELECT * FROM streak_records WHERE id = :id LIMIT 1")
    fun getStreak(id: String = "primary_streak"): Flow<StreakRecordEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStreak(streak: StreakRecordEntity)

    @Query("DELETE FROM streak_records")
    suspend fun clearStreak()
}
