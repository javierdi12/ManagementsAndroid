package com.moviles.managements.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.moviles.managements.models.Student
import com.moviles.managements.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(courseId: Int, viewModel: StudentViewModel,navController: NavController) {

    val allStudents by viewModel.students.collectAsState()
    val students = allStudents.filter { it.courseId == courseId }
    val isOffline by viewModel.showOfflineAlert
    val isLoading by viewModel.isLoading

    // Al cargar la pantalla, obtener estudiantes por curso
    LaunchedEffect(courseId) {
        viewModel.fetchStudentsByCourseId(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Estudiantes del curso") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("add_student") //navController da error
            }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (isOffline) {
                Text("Estás viendo datos sin conexión", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                if (students.isEmpty()) {
                    Text("No hay estudiantes para este curso.")
                } else {
                    LazyColumn {
                        items(students) { student ->
                            StudentItem(
                                student = student,
                                onEdit = {
                                    navController.navigate("edit_student/${student.id}")// aqui tambien da error el nav
                                },
                                onDelete = {
                                    viewModel.deleteStudent(student.id)
                                }
                            )
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentItem(
    student: Student,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {

        Text("Nombre: ${student.name}")
        Text("Email: ${student.email}")
        Text("Teléfono: ${student.phone}")

        Row(modifier = Modifier.padding(top = 4.dp)) {
            Button(
                onClick = onEdit,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Editar")
            }
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Eliminar")
            }
        }
    }
}
