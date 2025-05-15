package com.moviles.managements



import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
//import com.google.ai.client.generativeai.common.shared.Content
import com.moviles.managements.ui.theme.screens.StudentDetailActivity



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

//        composable("studentMenu") {
//            StudentDetailActivity.Content()
//        }
    }
}