package com.example.studenttasks

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false,
    val priority: Priority = Priority.MEDIUM
)

fun describeTask(task: Task): String =
    "${task.title} - ${task.priority}"
