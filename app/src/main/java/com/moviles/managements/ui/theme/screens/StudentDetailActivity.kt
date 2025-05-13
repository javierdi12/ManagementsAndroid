package com.moviles.managements.ui.theme.screens

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


class StudentDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManagementsTheme {
                val viewModel: StudentViewModel = viewModel()
                StudentScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    viewModel: StudentDetailViewModel,
    onBack: () -> Unit
) {
    val studentWithCourse by viewModel.studentWithCourse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val dataSource by viewModel.dataSource.collectAsState()

    if (dataSource == StudentDetailViewModel.DataSource.DATABASE) {
        AlertDialog(
            onDismissRequest = { /* No se puede dismiss */ },
            title = { Text("Información") },
            text = { Text("Estás viendo datos almacenados localmente porque no hay conexión a internet.") },
            confirmButton = {
                Button(onClick = { /* */ }) {
                    Text("OK")
                }
            }
        )
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando datos del estudiante...")
        }
    } else {
        studentWithCourse?.let { data ->
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text("Perfil del Estudiante") },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = data.student.name,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DetailItem(icon = Icons.Default.Email, text = data.student.email)
                            DetailItem(icon = Icons.Default.Phone, text = data.student.phone)

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Curso inscrito",
                                style = MaterialTheme.typography.titleMedium
                            )

                            data.course?.let { course ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.List,
                                        contentDescription = "Course",
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "${course.name} (ID: ${course.id})",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                                Text(
                                    text = course.description,
                                    modifier = Modifier.padding(start = 40.dp)
                                )
                            } ?: Text(
                                text = "No hay información del curso",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } ?: run {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("No se encontró información del estudiante")
            }
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview(showBackground = true)
@Composable
fun StudentScreenPreview(){
    ManagementsTheme {
        var viewModel: StudentViewModel = viewModel()
        StudentScreen(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val students by viewModel.students.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }


    LaunchedEffect(Unit) {
        Log.i("Activity", "Coming here???")
        viewModel.fetchStudents()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Students") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedStudent = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Student")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // Button with padding
            Button(
                modifier = Modifier
                    .padding(16.dp) // Add padding around the button
                    .fillMaxWidth(),
                onClick = { viewModel.fetchStudents() }
            ) {
                Text("Refresh Students")
            }

            // Spacer to ensure some space between button and the list
            Spacer(modifier = Modifier.height(8.dp))

            // Event List with remaining space
            StudentList(students, onEdit = { student ->
                selectedStudent = student
                showDialog = true
            }, onDelete = { student -> viewModel.deleteStudent(student.id) })
        }
        // Show dialog for adding or editing event
        if (showDialog) {
            StudentDialog(
                student = selectedStudent,
                onDismiss = { showDialog = false },
                onSave = { student ->
                    if (student.id == null) viewModel.addStudent(student)
                    else viewModel.updateStudent(student)
                    showDialog = false // Close dialog after saving
                }
            )
        }
    }
}

@Composable
fun StudentList(students: List<Student>, modifier: Modifier = Modifier, onEdit: (Student) -> Unit, onDelete: (Student) -> Unit) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(students) { student ->
            StudentItem(student, onEdit, onDelete)
        }
    }
}

@Composable
fun StudentItem(student: Student, onEdit: (Student) -> Unit, onDelete: (Student) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        elevation = CardDefaults.elevatedCardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = student.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = student.email, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Phone: ${student.phone}", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Course ID: ${student.courseId}", style = MaterialTheme.typography.bodyMedium)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { onEdit(student) }) {
                    Text("Edit", color = MaterialTheme.colorScheme.primary)
                }
                TextButton(onClick = { onDelete(student) }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDialog(
    student: Student?,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var courseIdText by remember { mutableStateOf(student?.courseId?.toString() ?: "") }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (student == null) "Add Student" else "Edit Student") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone*") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = courseIdText,
                    onValueChange = { courseIdText = it },
                    label = { Text("Course ID*") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = courseIdText.isNotBlank() && courseIdText.toIntOrNull() == null,
                    supportingText = {
                        if (courseIdText.isNotBlank() && courseIdText.toIntOrNull() == null) {
                            Text("Please enter a valid number")
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val courseId = courseIdText.toIntOrNull() ?: 0
                    onSave(
                        Student(
                            id = student?.id,
                            name = name,
                            email = email,
                            phone = phone,
                            courseId = courseId
                        )
                    )
                },
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}