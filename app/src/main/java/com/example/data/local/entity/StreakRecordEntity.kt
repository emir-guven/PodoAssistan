package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "streak_records")
data class StreakRecordEntity(
    @PrimaryKey val id: String = "primary_streak",
    val currentStreakDays: Int,
    val bestStreakDays: Int,
    val lastCheckDate: String,
    val totalChecksDone: Int
)
