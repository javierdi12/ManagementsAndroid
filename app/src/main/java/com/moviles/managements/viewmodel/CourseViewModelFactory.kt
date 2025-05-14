package com.moviles.managements.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.moviles.managements.data.local.CourseDao
import com.moviles.managements.network.ApiService

class CourseViewModelFactory(
    private val api: ApiService,
    private val dao: CourseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CourseViewModel(api, dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}