package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val phone: String,
    val tcKimlikNo: String,
    val age: Int,
    val bloodType: String,
    val diabetesStatus: String,
    val footRiskLevel: String,
    val chronicDiseases: String, // Comma separated
    val regularMedications: String, // Pipe separated
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val address: String,
    val podologistNotes: String
)
