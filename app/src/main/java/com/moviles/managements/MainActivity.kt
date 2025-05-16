package com.moviles.managements

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import com.moviles.managements.data.local.AppDatabase
import com.moviles.managements.network.RetrofitInstance
import com.moviles.managements.ui.theme.screens.CourseListScreen
import com.moviles.managements.ui.theme.screens.EditCourseScreen
import com.moviles.managements.viewmodel.CourseViewModel
import com.moviles.managements.viewmodel.CourseViewModelFactory

//
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.managements.ui.theme.screens.CreateCourseScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = AppDatabase.getDatabase(applicationContext).courseDao()
        val api = RetrofitInstance.getApiService(applicationContext)
        val factory = CourseViewModelFactory(api, dao)

        setContent {
            val viewModel: CourseViewModel = viewModel(factory = factory)
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "course_list") {
                //lista de cursos
                composable("course_list") {
                    CourseListScreen(
                        viewModel = viewModel,
                        onAddCourse = { navController.navigate("create_course") },
                        onEditCourse = { courseId -> navController.navigate("edit_course/$courseId") }
                    )
                }

                //crear curso
                composable("create_course") {
                    CreateCourseScreen(
                        viewModel = viewModel,
                        onCourseCreated = {
                            navController.popBackStack()
                        }
                    )
                }
                //editar curso
                composable("edit_course/{courseId}") { backStackEntry ->
                    val courseId = backStackEntry.arguments?.getString("courseId")?.toIntOrNull()
                    val course = viewModel.courses.collectAsState().value.find { it.id == courseId }

                    if (course != null) {
                        EditCourseScreen(
                            course = course,
                            viewModel = viewModel,
                            onCourseUpdated = {
                                navController.popBackStack()
                            }
                        )
                    }
                }



            }
        }
    }
}

