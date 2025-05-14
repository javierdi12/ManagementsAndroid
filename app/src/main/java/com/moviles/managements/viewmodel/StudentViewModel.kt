package com.moviles.managements.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.moviles.managements.data.local.AppDatabase
import com.moviles.managements.models.Course
import com.moviles.managements.models.Student
import com.moviles.managements.network.ApiService
import com.moviles.managements.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



import android.net.ConnectivityManager
import android.net.NetworkCapabilities



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




//    fun fetchStudents() {
//        viewModelScope.launch {
//            isLoading.value = true
//
//            if (!hasNetwork()) {
//                // Offline: carga lo que haya en la base
//                isLoadingFromLocal.value = true
//                Log.d("ROOM-DBG", "Offline → ${_students.value.size} estudiantes cargados de Room: ${_students.value}")
//                _students.value = db.studentDao().getAllStudents()
//                isLoading.value = false
//                return@launch
//            }
//
//            // Con red: refresca datos
//            try {
//                val netList = apiService.getStudents()
//                Log.d("API-DBG", "getStudents() devolvió ${netList.size} elementos: $netList")
//                _students.value = netList
//                db.studentDao().insertAll(netList)
//                val dbList = db.studentDao().getAllStudents()
//                Log.d("ROOM-DBG", "Tras insertAll → ${dbList.size} estudiantes en Room: $dbList")
//                showOfflineAlert.value = false
//            } catch (e: Exception) {
//                // Si falla la API, carga local si existe algo
//                db.studentDao().getAllStudents()
//                    .takeIf { it.isNotEmpty() }
//                    ?.let { _students.value = it }
//                showOfflineAlert.value = true
//                Log.e("StudentVM", "fetchStudents error: ${e.message}")
//            } finally {
//                isLoading.value = false
//            }
//        }
//    }
//
//    /**
//     * Si hay red, baja cursos y guarda en Room;
//     * si falla, carga lo que haya en Room.
//     */
//    fun fetchCourses() {
//        viewModelScope.launch {
//            try {
//                val net = apiService.getCourses()
//                _courses.value = net
//                db.courseDao().insertAll(net)
//            } catch (e: Exception) {
//                db.courseDao().getAllCourses()
//                    .takeIf { it.isNotEmpty() }
//                    ?.let { _courses.value = it }
//                Log.e("StudentVM", "fetchCourses error: ${e.message}")
//            }
//        }
//    }

//    fun fetchStudents() {
//        viewModelScope.launch {
//            isLoading.value = true
//
//            if (!hasNetwork()) {
//                val cached = db.studentDao().getAllStudents()
//                _students.value = cached
//
//                val ca = db.courseDao().getAllCourses()
//                _courses.value = ca
//                isLoadingFromLocal.value = true
//                Log.d("ROOM-DBG", "Offline → ${cached.size} estudiantes en Room: $cached")
//                Log.d("ROOM-DBG", "Offline → ${ca.size} cursos en Room: $cached")
//                isLoading.value = false
//                return@launch
//            }
//
//            try {
//                val netList = apiService.getStudents()
//                Log.d("API-DBG", "getStudents() devolvió ${netList.size} elementos")
//                _students.value = netList
//
//                db.studentDao().insertAll(netList)
//                showOfflineAlert.value = false
//
//            } catch (e: Exception) {
//                val fallback = db.studentDao().getAllStudents()
//                _students.value = fallback
//                showOfflineAlert.value = true
//                Log.e("StudentVM", "fetchStudents error: ${e.message}. Fallback → ${fallback.size}")
//            } finally {
//                isLoading.value = false
//            }
//        }
//    }

    fun fetchStudents() {
        viewModelScope.launch {
            try {
                val net = apiService.getStudents()
                _students.value = net
                db.studentDao().insertAll(net)
            } catch (e: Exception) {
                val cached = db.studentDao().getAllStudents()
                _students.value = cached
                Log.e("StudentVM", "fetchCourses error: ${e.message}")
                Log.d("ROOM-DBG", "Offline → ${cached.size} studiantes en Room")
            }
        }
    }

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                val net = apiService.getCourses()
                _courses.value = net
                db.courseDao().insertAll(net)
            } catch (e: Exception) {
                val cached = db.courseDao().getAllCourses()
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