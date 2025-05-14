package com.moviles.managements

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.moviles.managements.data.local.AppDatabase
import com.moviles.managements.network.RetrofitInstance
import com.moviles.managements.ui.theme.screens.CourseListScreen
import com.moviles.managements.viewmodel.CourseViewModel
import com.moviles.managements.viewmodel.CourseViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CourseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getDatabase(applicationContext).courseDao()
        val api = RetrofitInstance.getApi()
        val factory = CourseViewModelFactory(api, dao)

        // Crear el ViewModel de forma segura
        viewModel = CourseViewModel(api, dao)

        setContent {
            CourseListScreen(viewModel = viewModel)
        }
    }
}

