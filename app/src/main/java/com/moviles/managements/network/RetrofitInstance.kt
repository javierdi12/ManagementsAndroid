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
    private const val cacheSize = 10 * 1024 * 1024 // 10 MB

    fun create(context: Context): ApiService {
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize.toLong())

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor { chain ->
                if (!context.hasNetwork()) {
                    throw NoInternetException("Sin conexión a Internet")
                }

                val request = chain.request().newBuilder()
                    .header("Cache-Control", "public, max-age=5")
                    .build()

                chain.proceed(request)
            }

            .build()

        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

fun Context.hasNetwork(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}