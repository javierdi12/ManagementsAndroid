package com.moviles.managements.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.managements.database.AppDatabase
import com.moviles.managements.models.StudentWithCourse
import com.moviles.managements.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val studentDao = AppDatabase.getDatabase(application).studentDao()
    private val courseDao = AppDatabase.getDatabase(application).courseDao()
    private val apiService = RetrofitInstance.create(application)

    private val _studentWithCourse = MutableStateFlow<StudentWithCourse?>(null)
    val studentWithCourse: StateFlow<StudentWithCourse?> = _studentWithCourse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _dataSource = MutableStateFlow<DataSource?>(null)
    val dataSource: StateFlow<DataSource?> = _dataSource

    enum class DataSource {
        NETWORK, DATABASE
    }

    fun fetchStudentDetails(studentId: Int) {
        viewModelScope.launch {
            _isLoading.value = true

            try {

                val student = apiService.getStudent(studentId)
                val course = student.courseId?.let { apiService.getCourse(it) }


                student?.let { studentDao.insertStudent(it) }
                course?.let { courseDao.insertCourse(it) }

                _studentWithCourse.value = StudentWithCourse(student, course)
                _dataSource.value = DataSource.NETWORK
            } catch (e: Exception) {

                val localData = studentDao.getStudentWithCourse(studentId)
                _studentWithCourse.value = localData
                _dataSource.value = DataSource.DATABASE
            } finally {
                _isLoading.value = false
            }
        }
    }
}