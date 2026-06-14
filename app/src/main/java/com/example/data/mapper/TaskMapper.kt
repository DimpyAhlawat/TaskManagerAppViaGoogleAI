package com.example.data.mapper

import com.example.data.database.TaskEntity
import com.example.data.network.dto.TodoDto
import com.example.domain.model.Priority
import com.example.domain.model.Task

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = try { Priority.valueOf(priority) } catch (e: Exception) { Priority.MEDIUM },
        dueDate = dueDate,
        isCompleted = isCompleted,
        isLocal = isLocal
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority.name,
        dueDate = dueDate,
        isCompleted = isCompleted,
        isLocal = isLocal
    )
}

fun TodoDto.toEntity(): TaskEntity {
    val priority = when (id % 3) {
        0 -> Priority.LOW
        1 -> Priority.MEDIUM
        else -> Priority.HIGH
    }
    // Set a predictable due date offset from system time based on ID so it is stable but varied
    val daysInFuture = (id % 7).toLong()
    val dueDateOffset = daysInFuture * 24 * 60 * 60 * 1000
    // We can use a reference timestamp so that tests are stable!
    val baseTime = 1718300000000L // Approx June 2024 reference
    
    return TaskEntity(
        id = id.toString(),
        title = todo,
        description = "Task from cloud service. User owner: $userId",
        priority = priority.name,
        dueDate = baseTime + dueDateOffset,
        isCompleted = completed,
        isLocal = false
    )
}
