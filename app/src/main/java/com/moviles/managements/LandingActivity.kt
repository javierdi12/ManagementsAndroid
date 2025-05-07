package com.moviles.managements

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.managements.models.Student
import com.moviles.managements.ui.theme.ManagementsTheme
import com.moviles.managements.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


import androidx.lifecycle.LifecycleEventObserver


class LandingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ManagementsTheme {
                val viewModel: StudentViewModel = viewModel()
                val intent = Intent(this, MainActivity::class.java)
                LandingPage(viewModel) { startActivity(intent) }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(viewModel: StudentViewModel, onNavigate: () -> Unit) {
    val students by viewModel.students.collectAsState()
    val pagerState = rememberPagerState(pageCount = { students.size })
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Use OnLifecycleEvent to observe lifecycle state
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, student ->
            if (student == Lifecycle.Event.ON_RESUME) {
                viewModel.fetchStudents()  // Call the fetchEvents method when the activity is resumed
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Students") }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            HorizontalPager(state = pagerState) { page ->
                StudentCard(student = students[page])
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(students.size) { index ->
                    IconButton(onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    }) {
                        Icon(
                            painter = painterResource(id = if (pagerState.currentPage == index) android.R.drawable.presence_online else android.R.drawable.presence_invisible),
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigate() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Ver todos los estudiantes")
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Estudiantes destacados", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(students.size) { index ->
                    StudentCard(student = students[index], modifier = Modifier.padding(end = 8.dp))
                }
            }
        }
    }
}

@Composable
fun StudentCard(student: Student, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("👤 Nombre: ${student.name}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("📧 Email: ${student.email}", style = MaterialTheme.typography.bodyMedium)
            Text("📱 Teléfono: ${student.phone}", style = MaterialTheme.typography.bodyMedium)
            Text("📘 ID del curso: ${student.courseId}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}