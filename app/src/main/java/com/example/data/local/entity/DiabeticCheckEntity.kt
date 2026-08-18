package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diabetic_checks")
data class DiabeticCheckEntity(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val dateFormatted: String,
    val checkedYesCount: Int,
    val totalQuestions: Int,
    val riskLevel: String,
    val scoreText: String,
    val doctorAdvice: String
)
