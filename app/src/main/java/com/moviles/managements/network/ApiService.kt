package com.moviles.managements.network
import com.moviles.managements.models.Course
import com.moviles.managements.models.Student
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {
    @GET("api/student")
    suspend fun getStudents(): List<Student>


    @POST("api/student")
        suspend fun addStudent(@Body student: Student): Student

//    @POST("api/student")
//    suspend fun addStudent(@Part("name") name: RequestBody,
//                            @Part("email") email: RequestBody,
//                            @Part("phone") phone: RequestBody,
//                            @Part("courseId") courseId: RequestBody): Student

    @PUT("api/student/{id}")
    suspend fun updateStudent(@Path("id") id: Int?, @Body studentDto: Student): Student

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id: Int?): Response<Unit>

    @GET("api/student/{id}")
    suspend fun getStudent(@Path("id") id: Int): Student

    @GET("api/course/{id}")
    suspend fun getCourse(@Path("id") id: Int): Course
}