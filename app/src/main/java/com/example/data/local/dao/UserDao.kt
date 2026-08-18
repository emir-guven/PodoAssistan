package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DoctorProfileEntity
import com.example.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id LIMIT 1")
    fun getUserProfile(id: String = "patient_1"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(user: UserProfileEntity)

    @Query("SELECT * FROM doctor_profiles WHERE id = :id LIMIT 1")
    fun getDoctorProfile(id: String = "doc_1"): Flow<DoctorProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctorProfile(doctor: DoctorProfileEntity)

    @Query("DELETE FROM user_profiles")
    suspend fun clearUserProfiles()

    @Query("DELETE FROM doctor_profiles")
    suspend fun clearDoctorProfiles()
}
