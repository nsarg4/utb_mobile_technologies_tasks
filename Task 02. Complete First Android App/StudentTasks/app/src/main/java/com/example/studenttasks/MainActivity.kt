package com.example.studenttasks

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.studenttasks.ui.theme.StudentTasksTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudentTasksApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun StudentTasksApp(modifier: Modifier = Modifier) {
    var tasks by remember {
        mutableStateOf(
            listOf(
                Task(
                    id = 1,
                    title = "Prepare Kotlin exercise",
                    priority = Priority.HIGH
                ),
                Task(
                    id = 2,
                    title = "Read Android documentation",
                    priority = Priority.MEDIUM
                ),
                Task(
                    id = 3,
                    title = "Run the app",
                    isCompleted = true,
                    priority = Priority.LOW
                )
            )
        )
    }

    var newTaskTitle by rememberSaveable {
        mutableStateOf("")
    }

    val duplicateTitle = tasks.any { task ->
        task.title.equals(
            newTaskTitle.trim(),
            ignoreCase = true
        )
    }

    var selectedPriority by remember {
        mutableStateOf(Priority.MEDIUM)
    }

    val sortedTasks = tasks.sortedBy { task ->
        when (task.priority) {
            Priority.HIGH -> 0
            Priority.MEDIUM -> 1
            Priority.LOW -> 2
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Student Tasks",
            style = MaterialTheme.typography.headlineLarge
        )

        val completedCount = tasks.count { it.isCompleted }

        val progressPercent = if (tasks.isEmpty()) {
            0
        } else {
            completedCount * 100 / tasks.size
        }

        val pendingTasks = tasks.filter { !it.isCompleted }

        Text(
            text = "$completedCount of ${tasks.size} completed"
        )

        Text("Progress: $progressPercent%")


        OutlinedButton(
            onClick = {
                tasks = tasks.filter { !it.isCompleted }
            },
            enabled = tasks.any { it.isCompleted }
        ) {
            Text("Clear completed")
        }

        OutlinedTextField(
            value = newTaskTitle,
            onValueChange = { newValue ->
                newTaskTitle = newValue
            },
            label = {
                Text("New task")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Priority.entries.forEach { priority ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedPriority == priority,
                    onClick = {
                        selectedPriority = priority
                    }
                )

                Text(priority.name)
            }
        }

        Button(
            onClick = {
                val newTask = Task(
                    id = (tasks.maxOfOrNull { it.id } ?: 0) + 1,
                    title = newTaskTitle.trim(),
                    priority = selectedPriority

                )

                tasks = tasks + newTask
                newTaskTitle = ""
            },
            enabled = newTaskTitle.isNotBlank() && !duplicateTitle
        ) {
            Text("Add Task")
        }

        if (tasks.isEmpty()) {
            Text("No tasks yet.")
        } else {
            TaskList(
                tasks = sortedTasks,
                onCompletedChange = { changedTask, isCompleted ->
                    tasks = tasks.map { task ->
                        if (task.id == changedTask.id) {
                            task.copy(isCompleted = isCompleted)
                        } else {
                            task
                        }
                    }
                },
                onDelete = { taskToDelete ->
                    tasks = tasks.filter { task ->
                        task.id != taskToDelete.id
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
fun TaskRow(
    task: Task,
    onCompletedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCompletedChange
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = task.title,
                textDecoration = if (task.isCompleted) {
                    TextDecoration.LineThrough
                } else {
                    null
                }
            )

            Text(
                text = task.priority.name,
                style = MaterialTheme.typography.bodySmall
            )
        }

        OutlinedButton(
            onClick = onDelete
        ) {
            Text("Delete")
        }
    }
}

@Composable
fun CounterExample() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text("Tasks: $count")

        Button(
            onClick = {
                count++
            }
        ) {
            Text("Add")
        }
    }
}

@Composable
fun AddTaskExample() {
    var taskTitle by rememberSaveable {
        mutableStateOf("")
    }

    var addAttempts by remember {
        mutableStateOf(0)
    }

    Column {
        OutlinedTextField(
            value = taskTitle,
            onValueChange = { newValue ->
                taskTitle = newValue
            },
            label = {
                Text("Task title")
            }
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

            Button(
                onClick = {
                    addAttempts++
                },
                enabled = taskTitle.isNotBlank()
            ) {
                Text("Add Task")
            }

            Text("Tasks added: $addAttempts")

            Button(
                onClick = {
                    taskTitle = ""
                }
            ) {
                Text("Clear")
            }
        }

        Text("You typed: $taskTitle")
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onCompletedChange: (Task, Boolean) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = tasks,
            key = { task -> task.id }
        ) { task ->
            TaskRow(
                task = task,
                onCompletedChange = { isCompleted ->
                    onCompletedChange(task, isCompleted)
                },
                onDelete = {
                    onDelete(task)
                }
            )
        }
    }
}