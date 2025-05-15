package com.moviles.managements.ui.theme.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.moviles.managements.models.Student
import com.moviles.managements.ui.theme.ManagementsTheme
import com.moviles.managements.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.moviles.managements.models.Course
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider




class StudentDetailActivity : ComponentActivity() {

    private lateinit var viewModel: StudentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        viewModel = ViewModelProvider(this)[StudentViewModel::class.java]

        setContent {
            ManagementsTheme {

                StudentDetailScreen(
                    viewModel = viewModel,
                    onBack    = { finish() },
                    onViewAll = {
                        startActivity(Intent(this, StudentsActivity::class.java))
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //  Each time the Activity returns to the foreground,
        // reloads data (online or offline)

        viewModel.fetchCourses()
        viewModel.fetchStudents()


    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    viewModel: StudentViewModel,
    onBack:    () -> Unit,
    onViewAll: () -> Unit
) {
    val students        by viewModel.students.collectAsState()
    val courses         by viewModel.courses.collectAsState()
    val isLoading       by remember { derivedStateOf { viewModel.isLoading.value } }
    val isLoadingLocal  by remember { derivedStateOf { viewModel.isLoadingFromLocal.value } }
    val showOfflineFlag by remember { derivedStateOf { viewModel.showOfflineAlert.value } }

    val context = LocalContext.current
    var showOfflineDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { students.size })
    val coroutineScope = rememberCoroutineScope()

    // Toast when loading from Room
    LaunchedEffect(isLoadingLocal) {
        if (isLoadingLocal) {
            Toast.makeText(
                context,
                "Cargando datos desde almacenamiento local…",
                Toast.LENGTH_SHORT
            ).show()
            viewModel.isLoadingFromLocal.value = false
        }
    }


    LaunchedEffect(showOfflineFlag) {
        if (showOfflineFlag) {
            showOfflineDialog = true
        }
    }
    if (showOfflineDialog) {
        AlertDialog(
            onDismissRequest = { showOfflineDialog = false },
            title            = { Text("Modo Offline") },
            text             = { Text("Mostrando datos LocalStorage/Caché") },
            confirmButton    = {
                TextButton(onClick = { showOfflineDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = { Text("Detalles del Estudiante") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Cargando detalles…", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                students.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay detalles disponibles.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                else -> {

                    HorizontalPager(
                        state    = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) { page ->
                        StudentDetailCard(
                            student  = students[page],
                            courses  = courses,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }

                    Spacer(Modifier.height(8.dp))


                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(students.size) { index ->
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }) {
                                Icon(
                                    painter           = painterResource(
                                        id = if (pagerState.currentPage == index)
                                            android.R.drawable.presence_online
                                        else
                                            android.R.drawable.presence_invisible
                                    ),
                                    contentDescription = null
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))


                    Button(
                        onClick = onViewAll,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Text("Ver todos los estudiantes")
                    }
                }
            }
        }
    }
}

@Composable
fun StudentDetailCard(
    student: Student,
    courses: List<Course>,
    modifier: Modifier = Modifier
) {
    val courseName = remember(student.courseId, courses) {
        courses.firstOrNull { it.id == student.courseId }?.name ?: "Curso desconocido"
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("👤 Nombre: ${student.name}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("📧 Email:  ${student.email}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("📱 Teléfono: ${student.phone}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("📚 Curso:    $courseName", style = MaterialTheme.typography.bodyMedium)
        }
    }
}