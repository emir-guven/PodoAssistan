package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DiabeticCheckDao
import com.example.data.local.dao.StreakDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.DiabeticCheckEntity
import com.example.data.local.entity.DoctorProfileEntity
import com.example.data.local.entity.StreakRecordEntity
import com.example.data.local.entity.UserProfileEntity
import com.example.util.Constants

@Database(
    entities = [
        DiabeticCheckEntity::class,
        UserProfileEntity::class,
        DoctorProfileEntity::class,
        StreakRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PodoDatabase : RoomDatabase() {
    abstract fun diabeticCheckDao(): DiabeticCheckDao
    abstract fun userDao(): UserDao
    abstract fun streakDao(): StreakDao

    companion object {
        @Volatile
        private var INSTANCE: PodoDatabase? = null

        fun getDatabase(context: Context): PodoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PodoDatabase::class.java,
                    Constants.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
