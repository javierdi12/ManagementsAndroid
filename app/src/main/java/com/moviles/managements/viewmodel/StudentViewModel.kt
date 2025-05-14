package com.moviles.managements.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.managements.models.Student
import com.moviles.managements.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import android.content.Context
import android.net.Uri
import com.moviles.managements.models.Course
import com.moviles.managements.network.ApiService
import kotlinx.coroutines.flow.asStateFlow

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StudentViewModel(application: Application) : AndroidViewModel(application) {


    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses



    fun fetchStudents(){
        viewModelScope.launch {
            try {
                _students.value = RetrofitInstance.api.getStudents()
                Log.i("MyViewModel", "Fetching data from API... ${_students.value}")
            } catch (e: Exception){
                Log.e("ViewmodelError", "Error: ${e}")
            }
        }
    }

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _courses.value =  RetrofitInstance.api.getCourses()
                Log.i("MyViewModel", "Fetching data from API... ${_courses.value}")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error fetching courses: ${e.message}")
            }
        }
    }


    fun addStudent(student: Student) {
        viewModelScope.launch {
            try {
                val response =  RetrofitInstance.api.addStudent(student)
                _students.value += response
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun updateStudent(student: Student){
        viewModelScope.launch {
            try {
                Log.i("ViewModelInfo", "Event: ${student}")
                val response =  RetrofitInstance.api.updateStudent(student.id, student)
                _students.value = _students.value.map { student ->
                    if (student.id == response.id) response else student
                }
                Log.i("ViewModelInfo", "Response: ${response}")
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ViewModelError", "HTTP Error: ${e.message()}, Response Body: $errorBody")
            } catch (e: Exception) {
                Log.e("ViewModelError", "Error: ${e.message}", e)
            }
        }
    }

    fun deleteStudent(studentId: Int?) {
        studentId?.let { id ->
            viewModelScope.launch {
                try {
                    RetrofitInstance.api.deleteStudent(id)
                    _students.value = _students.value.filter { it.id != studentId }
                } catch (e: Exception) {
                    Log.e("ViewModelError", "Error deleting event: ${e.message}")
                }
            }
        } ?: Log.e("ViewModelError", "Error: studentId is null")
    }


}