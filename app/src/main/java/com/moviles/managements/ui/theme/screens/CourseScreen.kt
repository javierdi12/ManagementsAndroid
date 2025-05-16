package com.moviles.managements.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.moviles.managements.models.Course
import com.moviles.managements.viewmodel.CourseViewModel

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color


@Composable
fun CourseListScreen(viewModel: CourseViewModel, onAddCourse: () -> Unit, onEditCourse: (Int) -> Unit) {

    val courses = viewModel.courses.collectAsState()
    val isFromCache = viewModel.isFromCache.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCourse) {
                Text("+")
            }

        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val showOfflineDialog = remember(isFromCache.value) { mutableStateOf(isFromCache.value) }


            if (showOfflineDialog.value) {
                AlertDialog(
                    onDismissRequest = { showOfflineDialog.value = false },
                    title = { Text("Modo Offline") },
                    text = { Text("Mostrando datos desde almacenamiento local") },
                    confirmButton = {
                        TextButton(onClick = { showOfflineDialog.value = false }) {
                            Text("OK")
                        }

                    }
                )
            }


            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(courses.value) { course ->
                    CourseItem(course = course, onClick = { onEditCourse(course.id) })
                }
            }
        }
    }
}

@Composable
fun CourseItem(course: Course, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                //Si se usa emulador cambiar la ip acá por la del api local host
                painter = rememberAsyncImagePainter("http://192.168.2.3:5000/uploads/${course.imageUrl}"),
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

//Uri to file
fun uriToFile(context: Context, uri: Uri): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val fileName = getFileName(context, uri)
    val file = File(context.cacheDir, fileName)
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return file
}

fun getFileName(context: Context, uri: Uri): String {
    var name = "image_${System.currentTimeMillis()}.jpg"
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    returnCursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst()) {
            name = it.getString(nameIndex)
        }
    }
    return name
}

//create course
@Composable
fun CreateCourseScreen(
    viewModel: CourseViewModel,
    onCourseCreated: () -> Unit
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val schedule = remember { mutableStateOf("") }
    val professor = remember { mutableStateOf("") }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageFile = remember { mutableStateOf<File?>(null) }

    // Selector de imagen
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            imageFile.value = uriToFile(context, it)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") })
        OutlinedTextField(value = description.value, onValueChange = { description.value = it }, label = { Text("Descripción") })
        OutlinedTextField(value = schedule.value, onValueChange = { schedule.value = it }, label = { Text("Horario") })
        OutlinedTextField(value = professor.value, onValueChange = { professor.value = it }, label = { Text("Profesor") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Seleccionar Imagen")
        }

        imageUri.value?.let {
            Text("Imagen seleccionada: ${it.lastPathSegment}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val uri = imageUri.value
                if (uri != null) {
                    viewModel.createCourse(
                        name = name.value,
                        description = description.value,
                        schedule = schedule.value,
                        professor = professor.value,
                        imageUri = uri,
                        context = context,
                        onSuccess = {
                            onCourseCreated()
                        },
                        onError = {
                            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            enabled = imageUri.value != null
        ) {
            Text("Crear Curso")
        }

    }
}

//editar curso
@Composable
fun EditCourseScreen(
    course: Course,
    viewModel: CourseViewModel,
    onCourseUpdated: () -> Unit
) {
    val context = LocalContext.current
    val name = remember { mutableStateOf(course.name) }
    val description = remember { mutableStateOf(course.description) }
    val schedule = remember { mutableStateOf(course.schedule) }
    val professor = remember { mutableStateOf(course.professor) }
    val imageUri = remember { mutableStateOf<Uri?>(null) }
    val imageFile = remember { mutableStateOf<File?>(null) }
    val showDialog = remember { mutableStateOf(false) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri.value = it
            imageFile.value = uriToFile(context, it)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = name.value, onValueChange = { name.value = it }, label = { Text("Nombre") })
        OutlinedTextField(value = description.value, onValueChange = { description.value = it }, label = { Text("Descripción") })
        OutlinedTextField(value = schedule.value, onValueChange = { schedule.value = it }, label = { Text("Horario") })
        OutlinedTextField(value = professor.value, onValueChange = { professor.value = it }, label = { Text("Profesor") })

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Cambiar Imagen")
        }

        imageUri.value?.let {
            Text("Imagen seleccionada: ${it.lastPathSegment}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val updatedCourse = course.copy(
                    name = name.value,
                    description = description.value,
                    schedule = schedule.value,
                    professor = professor.value
                )

                viewModel.updateCourseWithImage(
                    course = updatedCourse,
                    imageUri = imageUri.value,
                    context = context,
                    onSuccess = {
                        onCourseUpdated()
                    },
                    onError = {
                        Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                    }
                )
            }
        ) {
            Text("Actualizar Curso")
        }

        //boton eliminar curso

        Button(
            onClick = { showDialog.value = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Eliminar Curso", color = Color.White)
        }
        //fuera del botón
        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog.value = false
                        viewModel.deleteCourse(
                            courseId = course.id,
                            onSuccess = {
                                Toast.makeText(context, "Curso eliminado", Toast.LENGTH_SHORT).show()
                                onCourseUpdated()
                            },
                            onError = {
                                Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
                            }
                        )
                    }) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog.value = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("¿Eliminar curso?") },
                text = { Text("Esta acción no se puede deshacer.") }
            )
        }


    }
}

