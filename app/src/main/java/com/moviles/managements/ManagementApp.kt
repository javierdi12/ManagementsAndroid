package com.moviles.managements



import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.moviles.managements.ui.theme.screens.StudentMenuActivity
import com.moviles.managements.ui.theme.screens.StudentDetailActivity


/**
 * The root composable of the app.
 * Manages:
 * - Authentication state (logged in or not)
 * - Navigation between Login and Main App
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun ManagementApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            LandingScreen()
        }

        composable("studentMenu") {
            StudentMenuActivity.Content()
        }
    }
}