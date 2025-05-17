package com.moviles.managements.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.managements.data.local.AppDatabase
import com.moviles.managements.models.Course
import com.moviles.managements.models.Student

import com.moviles.managements.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class StudentViewModel(app: Application) : AndroidViewModel(app) {


    private val db         = AppDatabase.getDatabase(app.applicationContext)
    private val apiService = RetrofitInstance.getApiService(app.applicationContext)


    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> = _courses.asStateFlow()


    val isLoadingFromLocal = mutableStateOf(false)
    val showOfflineAlert   = mutableStateOf(false)
    val isLoading          = mutableStateOf(false)

    private val _studentDetail = MutableStateFlow<Student?>(null)
    val studentDetail: StateFlow<Student?> = _studentDetail.asStateFlow()


    /** 1) TRAER LISTA **/
    fun fetchStudents() {
        viewModelScope.launch {
            isLoading.value = true

            // OFFLINE puro: cargar solo de Room y salir
            if (!hasNetwork()) {
                loadStudentsFromCache()
                isLoading.value = false
                return@launch
            }

            // ONLINE: intento API
            try {
                val netList = apiService.getStudents()
                _students.value = netList

                // Actualizo caché en background
                withContext(Dispatchers.IO) {
                    db.studentDao().insertAll(netList)
                }

                // restauro flags online
                isLoadingFromLocal.value = false
                showOfflineAlert.value   = false

            } catch (e: Exception) {
                Log.e("StudentVM", "Error fetchStudents online, fallback a cache: ${e.message}")
                loadStudentsFromCache()
            } finally {
                isLoading.value = false
            }
        }
    }

    private suspend fun loadStudentsFromCache() {
        val local = withContext(Dispatchers.IO) {
            db.studentDao().getAllStudents()
        }
        _students.value = local
        isLoadingFromLocal.value = true
        showOfflineAlert.value   = true
        Log.d("StudentVM", "Cargando lista desde Room (items=${local.size})")
    }



    private suspend fun loadStudentDetailFromCache(id: Int) {
        val local = withContext(Dispatchers.IO) {
            db.studentDao().getStudentById(id)
        }
        _studentDetail.value = local
        isLoadingFromLocal.value = true
        showOfflineAlert.value   = true
        Log.d("StudentVM", "Cargando detalle desde Room → $local")
    }



    fun fetchCourses() {
        viewModelScope.launch {
            try {
                val net = apiService.getCourses()
                _courses.value = net
                db.courseDao().insertAll(net)
            } catch (e: Exception) {
                val cached = db.courseDao().getAllCoursesSql()
                _courses.value = cached
                Log.e("StudentVM", "fetchCourses error: ${e.message}")
                Log.d("ROOM-DBG", "Offline → ${cached.size} cursos en Room")
            }
        }
    }


    fun addStudent(student: Student) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.addStudent(student)
                cacheStudent(response)
            } catch (e: Exception) {
                // Offline:
                student.id = null
                cacheStudent(student)
                showOfflineAlert.value = true
                Log.e("StudentVM", "addStudent offline, saved locally: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            isLoading.value = true
            try {
                val response = apiService.updateStudent(student.id, student)
                cacheStudent(response)
            } catch (e: Exception) {
                cacheStudent(student)
                showOfflineAlert.value = true
                Log.e("StudentVM", "updateStudent offline, cached locally: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }

    fun deleteStudent(studentId: Int?) {
        studentId?.let { id ->
            viewModelScope.launch {
                isLoading.value = true
                try {
                    apiService.deleteStudent(id)
                    db.studentDao().deleteById(id)
                    _students.value = _students.value.filter { it.id != id }
                } catch (e: Exception) {
                    // Offline:
                    db.studentDao().deleteById(id)
                    _students.value = _students.value.filter { it.id != id }
                    showOfflineAlert.value = true
                    Log.e("StudentVM", "deleteStudent offline, removed locally: ${e.message}")
                } finally {
                    isLoading.value = false
                }
            }
        } ?: Log.e("StudentVM", "deleteStudent: studentId is null")
    }

    //obtener estudiantes por curso
    fun fetchStudentsByCourseId(courseId: Int) {
        viewModelScope.launch {
            isLoading.value = true

            try {
                val result = apiService.getStudentsByCourseId(courseId)
                _students.value = result

                // Opcional: cachear
                withContext(Dispatchers.IO) {
                    db.studentDao().insertAll(result)
                }

                isLoadingFromLocal.value = false
                showOfflineAlert.value = false

            } catch (e: Exception) {
                // fallback a Room si hay datos guardados
                val local = withContext(Dispatchers.IO) {
                    db.studentDao().getStudentsByCourse(courseId)
                }
                _students.value = local
                showOfflineAlert.value = true
                isLoadingFromLocal.value = true
                Log.e("StudentVM", "Error fetchByCourse, fallback: ${e.message}")
            } finally {
                isLoading.value = false
            }
        }
    }


    private suspend fun cacheStudent(student: Student) {
        db.studentDao().insert(student)
        _students.value = (_students.value.filter { it.id != student.id } + student)
            .distinctBy { it.id }
    }


    private fun hasNetwork(): Boolean {
        val cm = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }



}