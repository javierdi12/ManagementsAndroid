package com.moviles.managements.ui.theme.screens

import android.os.Bundle
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.moviles.managements.models.Course

import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem



class StudentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val courseId = intent.getIntExtra("courseId", -1)

        setContent {
            ManagementsTheme {
                val viewModel: StudentViewModel = viewModel()
                StudentScreen(viewModel, courseId)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun StudentScreenPreview(){
    ManagementsTheme {
        val viewModel: StudentViewModel = viewModel()
        StudentScreen(viewModel, courseId = -1)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(
    viewModel: StudentViewModel,
    courseId: Int
) {
    val students by viewModel.students.collectAsState()
    val courses by viewModel.courses.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var refreshCounter by remember { mutableStateOf(0) }

    LaunchedEffect(refreshCounter) {
        viewModel.fetchStudents()
        viewModel.fetchCourses()
    }

    // Filtrar estudiantes por el curso recibido
    val filteredStudents = remember(students, courseId) {
        if (courseId != -1) students.filter { it.courseId == courseId } else students
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Students") })
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
            Button(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                onClick = { refreshCounter++ }
            ) {
                Text("Refresh Data")
            }

            Spacer(modifier = Modifier.height(8.dp))

            StudentList(
                students = filteredStudents, // ✨ CAMBIO
                courses = courses,
                onEdit = { student ->
                    selectedStudent = student
                    showDialog = true
                },
                onDelete = { student ->
                    viewModel.deleteStudent(student.id)
                    refreshCounter++
                }
            )
        }

        if (showDialog) {
            StudentDialog(
                student = selectedStudent,
                courses = courses,
                onDismiss = { showDialog = false },
                onSave = { student ->
                    if (student.id == null){
                        viewModel.addStudent(student)
                    } else{
                        viewModel.updateStudent(student)
                    }
                    showDialog = false
                    refreshCounter++
                }
            )
        }
    }
}


@Composable
fun StudentList(
    students: List<Student>,
    courses: List<Course>,
    modifier: Modifier = Modifier,
    onEdit: (Student) -> Unit,
    onDelete: (Student) -> Unit
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(students) { student ->
            StudentItem(
                student = student,
                courses = courses,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }
}

@Composable
fun StudentItem(
    student: Student,
    courses: List<Course>,
    onEdit: (Student) -> Unit,
    onDelete: (Student) -> Unit
) {
    val courseName = remember(student.courseId, courses) {
        courses.firstOrNull { it.id == student.courseId }?.name ?: "Unknown Course"
    }

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
            Text(text = "Course: $courseName", style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
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
    courses: List<Course>,
    onDismiss: () -> Unit,
    onSave: (Student) -> Unit
) {
    var name by remember { mutableStateOf(student?.name ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }
    var phone by remember { mutableStateOf(student?.phone ?: "") }
    var selectedCourse by remember { mutableStateOf<Course?>(courses.find { it.id == student?.courseId }) }

    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (student == null) "Add student" else "Edit student") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Combobox course
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedCourse?.name ?: "Select course",
                        onValueChange = {},
                        label = { Text("Course") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.name) },
                                onClick = {
                                    selectedCourse = course
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updatedStudent = Student(
                    id = student?.id,
                    name = name,
                    email = email,
                    phone = phone,
                    courseId = selectedCourse?.id
                )
                onSave(updatedStudent)
            }) {
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