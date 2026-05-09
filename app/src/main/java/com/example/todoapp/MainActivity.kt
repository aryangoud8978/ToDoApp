package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.data.Task
import com.example.todoapp.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            MaterialTheme(

                colorScheme =
                    if (isSystemInDarkTheme()) {
                        darkColorScheme()
                    } else {
                        lightColorScheme()
                    }

            ) {

                TodoScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(viewModel: TaskViewModel) {

    var taskText by remember {
        mutableStateOf("")
    }

    val tasks by viewModel.tasks.collectAsState()

    Scaffold(

        topBar = {

            TopAppBar(
                title = {
                    Text(
                        text = "My Tasks",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            OutlinedTextField(
                value = taskText,

                onValueChange = {
                    taskText = it
                },

                label = {
                    Text("Enter Task")
                },

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {

                    if (taskText.isNotBlank()) {

                        viewModel.addTask(taskText)

                        taskText = ""
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
            ) {

                Text(
                    text = "Add Task",
                    fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (tasks.isEmpty()) {

                EmptyTaskScreen()

            } else {

                LazyColumn {

                    items(tasks) { task ->

                        TaskItem(
                            task = task,

                            onDelete = {
                                viewModel.deleteTask(task)
                            },

                            onTaskChecked = { isChecked ->

                                viewModel.updateTask(
                                    task.copy(
                                        isCompleted = isChecked
                                    )
                                )
                            },

                            onTaskUpdated = { updatedTask ->
                                viewModel.updateTask(updatedTask)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyTaskScreen() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = Icons.Default.List,
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No Tasks Yet",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add your first task above",
            fontSize = 16.sp
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onDelete: () -> Unit,
    onTaskChecked: (Boolean) -> Unit,
    onTaskUpdated: (Task) -> Unit
) {

    var showDialog by remember {
        mutableStateOf(false)
    }

    var updatedText by remember {
        mutableStateOf(task.title)
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = task.isCompleted,

                    onCheckedChange = {
                        onTaskChecked(it)
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = task.title,

                    fontSize = 18.sp,

                    textDecoration =
                        if (task.isCompleted)
                            TextDecoration.LineThrough
                        else
                            TextDecoration.None
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                FilledTonalButton(
                    onClick = {
                        showDialog = true
                    }
                ) {

                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Edit")
                }

                FilledTonalButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text("Delete")
                }
            }
        }
    }

    if (showDialog) {

        AlertDialog(

            onDismissRequest = {
                showDialog = false
            },

            title = {
                Text("Edit Task")
            },

            text = {

                OutlinedTextField(
                    value = updatedText,

                    onValueChange = {
                        updatedText = it
                    },

                    label = {
                        Text("Task")
                    }
                )
            },

            confirmButton = {

                Button(
                    onClick = {

                        onTaskUpdated(
                            task.copy(
                                title = updatedText
                            )
                        )

                        showDialog = false
                    }
                ) {
                    Text("Save")
                }
            },

            dismissButton = {

                OutlinedButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}