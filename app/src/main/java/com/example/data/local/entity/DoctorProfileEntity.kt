package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctor_profiles")
data class DoctorProfileEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val title: String,
    val hospital: String,
    val clinicAddress: String,
    val phone: String,
    val workingHours: String,
    val about: String,
    val specialties: String, // Comma separated
    val locationLat: Double,
    val locationLng: Double,
    val isVerified: Boolean,
    val verificationStatus: String,
    val diplomaUri: String?,
    val tcKimlikNo: String,
    val diplomaRegistryNo: String
)
