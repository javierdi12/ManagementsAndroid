package com.moviles.managements.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moviles.managements.dao.CourseDao
import com.moviles.managements.dao.StudentDao
import com.moviles.managements.models.Course
import com.moviles.managements.models.Student

@Database(
    entities = [Student::class, Course::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun courseDao(): CourseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "management_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}