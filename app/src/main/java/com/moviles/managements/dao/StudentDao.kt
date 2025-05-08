package com.moviles.managements.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.OnConflictStrategy
import com.moviles.managements.models.Student
import com.moviles.managements.models.StudentWithCourse

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Query("SELECT * FROM Students WHERE id = :studentId")
    suspend fun getStudent(studentId: Int): Student?

    @Transaction
    @Query("SELECT * FROM Students WHERE id = :studentId")
    suspend fun getStudentWithCourse(studentId: Int): StudentWithCourse?

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)
}