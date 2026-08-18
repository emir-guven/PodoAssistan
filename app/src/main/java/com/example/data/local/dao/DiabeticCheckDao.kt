package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DiabeticCheckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiabeticCheckDao {
    @Query("SELECT * FROM diabetic_checks ORDER BY timestamp DESC")
    fun getAllChecks(): Flow<List<DiabeticCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: DiabeticCheckEntity)

    @Query("DELETE FROM diabetic_checks WHERE id = :id")
    suspend fun deleteCheckById(id: String)

    @Query("DELETE FROM diabetic_checks")
    suspend fun clearAllChecks()
}
