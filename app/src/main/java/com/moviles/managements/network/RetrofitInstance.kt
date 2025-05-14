package com.moviles.managements.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.moviles.managements.common.Constants.API_BASE_URL
import okhttp3.Cache
import java.io.File

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

import okhttp3.OkHttpClient


object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL) // Change to your API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}