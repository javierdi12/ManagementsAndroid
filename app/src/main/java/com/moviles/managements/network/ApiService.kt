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
    //------------------------------------------------------------------------
    //Course
    @GET("api/Course")
    suspend fun getCourses(): List<Course>

    @GET("api/Course/{id}")
    suspend fun getCourseById(@Path("id") id: Int): Course

    @Multipart
    @POST("api/Course")
    suspend fun createCourse(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("schedule") schedule: RequestBody,
        @Part("professor") professor: RequestBody,
        @Part image: MultipartBody.Part
    ): Course

    @Multipart
    @PUT("api/Course/{id}")
    suspend fun updateCourse(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("schedule") schedule: RequestBody,
        @Part("professor") professor: RequestBody,
        @Part image: MultipartBody.Part?
    ): Course

    @DELETE("api/Course/{id}")
    suspend fun deleteCourse(@Path("id") id: Int): Response<Unit>
    //----------------------------------------------------------

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