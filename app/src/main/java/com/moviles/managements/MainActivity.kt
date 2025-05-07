package com.moviles.managements

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.ui.text.input.TextFieldValue
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.clickable

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
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


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ManagementsTheme {
        Greeting("Android")
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
