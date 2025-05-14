package com.moviles.managements.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.moviles.managements.models.Course
import com.moviles.managements.viewmodel.CourseViewModel

@Composable
fun CourseListScreen(viewModel: CourseViewModel) {
    val courses = viewModel.courses.collectAsState()
    val isFromCache = viewModel.isFromCache.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // Acción para crear nuevo curso
            }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (isFromCache.value) {
                AlertDialog(
                    onDismissRequest = {},
                    title = { Text("Modo Offline") },
                    text = { Text("Mostrando datos desde almacenamiento local") },
                    confirmButton = {
                        TextButton(onClick = {}) {
                            Text("OK")
                        }
                    }
                )
            }

            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(courses.value) { course ->
                    CourseItem(course)
                }
            }
        }
    }
}

@Composable
fun CourseItem(course: Course) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter("http://10.0.2.2:5000/uploads/${course.imageUrl}"),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(course.name, style = MaterialTheme.typography.titleMedium)
                Text(course.description)
                Text("Horario: ${course.schedule}")
                Text("Profesor: ${course.professor}")
            }
        }
    }
}
