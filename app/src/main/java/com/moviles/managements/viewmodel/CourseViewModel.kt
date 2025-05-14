package com.moviles.managements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.managements.data.local.CourseDao
import com.moviles.managements.models.Course
import com.moviles.managements.network.ApiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class CourseViewModel(
    private val api: ApiService,
    private val dao: CourseDao
) : ViewModel() {

    // Lista de cursos observables para la UI
    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()

    // Indica si los datos vienen de Room (para mostrar alerta)
    private val _isFromCache = MutableStateFlow(false)
    val isFromCache: StateFlow<Boolean> = _isFromCache.asStateFlow()

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            try {
                val response = api.getCourses()
                // Guardar en Room
                dao.insertCourses(response)
                _courses.value = response
                _isFromCache.value = false
            } catch (e: IOException) {
                // Sin conexión, cargar de Room
                dao.getAllCourses().collect { localCourses ->
                    _courses.value = localCourses
                    _isFromCache.value = true
                }
            } catch (e: HttpException) {
                // Error de servidor u otro
                dao.getAllCourses().collect { localCourses ->
                    _courses.value = localCourses
                    _isFromCache.value = true
                }
            }
        }
    }

    fun deleteCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                api.deleteCourse(courseId)
                dao.deleteCourseById(courseId)
                loadCourses()
            } catch (e: Exception) {
                // Opcional: manejar error de eliminación
            }
        }
    }

    fun insertCourse(course: Course) {
        viewModelScope.launch {
            try {
                dao.insertCourse(course) // si solo es local de momento
                loadCourses()
            } catch (e: Exception) {
                // Manejar error si falla
            }
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            try {
                dao.insertCourse(course) // actualizar localmente
                loadCourses()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}

