package com.moviles.managements

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.managements.models.Student
import com.moviles.managements.ui.theme.ManagementsTheme
import com.moviles.managements.viewmodel.StudentViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import com.moviles.managements.viewmodel.StudentDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManagementsTheme {
                MainScreen {
                    startActivity(Intent(this, StudentDetailActivity::class.java))
                }
            }
        }
    }
}

@Composable
fun MainScreen(onManageStudentsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = onManageStudentsClick) {
            Text("Administrar Estudiantes", style = MaterialTheme.typography.bodyLarge)
        }
    }
}


//@Composable
//fun DetailItem(icon: ImageVector, text: String) {
//    Row(
//        modifier = Modifier.padding(vertical = 4.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            imageVector = icon,
//            contentDescription = null,
//            modifier = Modifier.size(24.dp)
//        )
//        Spacer(modifier = Modifier.width(16.dp))
//        Text(text = text, style = MaterialTheme.typography.bodyLarge)
//    }
//}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun StudentDetailScreen(
//    viewModel: StudentDetailViewModel,
//    onBack: () -> Unit
//) {
//    val studentWithCourse by viewModel.studentWithCourse.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val dataSource by viewModel.dataSource.collectAsState()
//
//    if (dataSource == StudentDetailViewModel.DataSource.DATABASE) {
//        AlertDialog(
//            onDismissRequest = { /* No se puede dismiss */ },
//            title = { Text("Información") },
//            text = { Text("Estás viendo datos almacenados localmente porque no hay conexión a internet.") },
//            confirmButton = {
//                Button(onClick = { /* */ }) {
//                    Text("OK")
//                }
//            }
//        )
//    }
//
//    if (isLoading) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            CircularProgressIndicator()
//            Spacer(modifier = Modifier.height(16.dp))
//            Text("Cargando datos del estudiante...")
//        }
//    } else {
//        studentWithCourse?.let { data ->
//            Scaffold(
//                topBar = {
//                    CenterAlignedTopAppBar(
//                        title = { Text("Perfil del Estudiante") },
//                        navigationIcon = {
//                            IconButton(onClick = onBack) {
//                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                            }
//                        }
//                    )
//                }
//            ) { padding ->
//                Column(
//                    modifier = Modifier
//                        .padding(padding)
//                        .padding(16.dp)
//                ) {
//                    Card(
//                        modifier = Modifier.fillMaxWidth(),
//                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(16.dp)) {
//                            Text(
//                                text = data.student.name,
//                                style = MaterialTheme.typography.headlineMedium
//                            )
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            DetailItem(icon = Icons.Default.Email, text = data.student.email)
//                            DetailItem(icon = Icons.Default.Phone, text = data.student.phone)
//
//                            Spacer(modifier = Modifier.height(16.dp))
//
//                            Text(
//                                text = "Curso inscrito",
//                                style = MaterialTheme.typography.titleMedium
//                            )
//
//                            data.course?.let { course ->
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.padding(vertical = 4.dp)
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.List,
//                                        contentDescription = "Course",
//                                        modifier = Modifier.size(24.dp)
//                                    )
//                                    Spacer(modifier = Modifier.width(16.dp))
//                                    Text(
//                                        text = "${course.name} (ID: ${course.id})",
//                                        style = MaterialTheme.typography.bodyLarge
//                                    )
//                                }
//                                Text(
//                                    text = course.description,
//                                    modifier = Modifier.padding(start = 40.dp)
//                                )
//                            } ?: Text(
//                                text = "No hay información del curso",
//                                style = MaterialTheme.typography.bodyMedium,
//                                color = MaterialTheme.colorScheme.error
//                            )
//                        }
//                    }
//                }
//            }
//        } ?: run {
//            Column(
//                modifier = Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("No se encontró información del estudiante")
//            }
//        }
//    }
//}

