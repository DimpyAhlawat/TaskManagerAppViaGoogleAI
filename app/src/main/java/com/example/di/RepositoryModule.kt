package com.example.di

import com.example.data.database.TaskDao
import com.example.data.network.TaskApi
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.repository.TaskRepository

class RepositoryModule(
    private val taskDao: TaskDao,
    private val taskApi: TaskApi
) {
    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(taskDao, taskApi)
    }
}
