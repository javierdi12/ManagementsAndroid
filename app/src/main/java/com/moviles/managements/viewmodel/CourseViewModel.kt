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

//
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream


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
                dao.deleteAllCourses()
                dao.insertCourses(response)
                _courses.value = response
                _isFromCache.value = false
            } catch (e: IOException) {
                // Sin conexión, cargar de Room
                val localCourses = dao.getAllCourses().first()
                _courses.value = localCourses
                _isFromCache.value = true
            } catch (e: HttpException) {
                // Error de servidor u otro
                val localCourses = dao.getAllCourses().first()
                _courses.value = localCourses
                _isFromCache.value = true
            }
        }
    }


    fun deleteCourse(
        courseId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                api.deleteCourse(courseId)
                dao.deleteCourseById(courseId)
                loadCourses()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al eliminar curso")
            }
        }
    }


    fun createCourse(
        name: String,
        description: String,
        schedule: String,
        professor: String,
        imageUri: Uri,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Convertir el Uri en un archivo temporal
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                // Preparar el MultipartBody.Part (imagen)
                val imageRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("image", file.name, imageRequestBody)

                // Preparar los otros campos como RequestBody
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val schedulePart = schedule.toRequestBody("text/plain".toMediaTypeOrNull())
                val professorPart = professor.toRequestBody("text/plain".toMediaTypeOrNull())

                // Llamar a la API
                val createdCourse = api.createCourse(
                    name = namePart,
                    description = descriptionPart,
                    schedule = schedulePart,
                    professor = professorPart,
                    image = imagePart
                )

                // Guardar en Room
                dao.insertCourse(createdCourse)

                // Recargar lista
                loadCourses()

                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error al crear curso")
            }
        }
    }


    //actualizar curso
    fun updateCourseWithImage(
        course: Course,
        imageUri: Uri?,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val namePart = course.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val descriptionPart = course.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val schedulePart = course.schedule.toRequestBody("text/plain".toMediaTypeOrNull())
                val professorPart = course.professor.toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageUri?.let {
                    val inputStream = context.contentResolver.openInputStream(it)
                    val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                    val outputStream = FileOutputStream(file)
                    inputStream?.copyTo(outputStream)
                    inputStream?.close()
                    outputStream.close()

                    val imageRequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("image", file.name, imageRequestBody)
                }

                val updatedCourse = api.updateCourse(
                    id = course.id,
                    name = namePart,
                    description = descriptionPart,
                    schedule = schedulePart,
                    professor = professorPart,
                    image = imagePart // puede ser null
                )

                dao.insertCourse(updatedCourse) // actualizar local
                loadCourses()
                onSuccess()

            } catch (e: Exception) {
                onError(e.message ?: "Error al actualizar curso")
            }
        }
    }

}

