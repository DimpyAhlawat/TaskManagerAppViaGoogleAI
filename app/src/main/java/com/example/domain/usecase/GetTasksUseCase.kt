package com.example.domain.usecase

import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(
        searchQuery: String = "",
        priorityFilter: Priority? = null,
        statusFilter: Boolean? = null
    ): Flow<List<Task>> {
        return repository.getTasksFlow().map { tasks ->
            tasks.filter { task ->
                val matchesQuery = task.title.contains(searchQuery, ignoreCase = true) ||
                        task.description.contains(searchQuery, ignoreCase = true)
                val matchesPriority = priorityFilter == null || task.priority == priorityFilter
                val matchesStatus = statusFilter == null || task.isCompleted == statusFilter
                matchesQuery && matchesPriority && matchesStatus
            }
        }
    }
}
