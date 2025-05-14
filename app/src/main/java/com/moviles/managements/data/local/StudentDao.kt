
package com.moviles.managements.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.managements.models.Student




@Dao
interface StudentDao {
    @Query("SELECT * FROM Students")
    suspend fun getAllStudents(): @JvmSuppressWildcards List<Student>

    @Query("SELECT * FROM Students WHERE id = :id")
    suspend fun getStudentById(id: Int): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)

    @Delete
    suspend fun delete(student: Student)

    @Query("DELETE FROM Students WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    @Query("DELETE FROM Students")
    suspend fun deleteAll(): Int

}
