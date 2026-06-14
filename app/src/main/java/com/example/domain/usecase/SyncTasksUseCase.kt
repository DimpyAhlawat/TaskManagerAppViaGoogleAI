package com.example.domain.usecase

import com.example.domain.repository.TaskRepository

class SyncTasksUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.fetchTasksFromNetwork()
    }
}
