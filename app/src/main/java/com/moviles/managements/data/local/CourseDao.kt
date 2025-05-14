package com.moviles.managements.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.managements.models.Course


@Dao
interface CourseDao {
    @Query("SELECT * FROM Courses")
    suspend fun getAllCourses(): @JvmSuppressWildcards List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>)
}