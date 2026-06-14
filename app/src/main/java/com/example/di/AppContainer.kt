package com.example.di

import android.content.Context
import com.example.domain.usecase.AddTaskUseCase
import com.example.domain.usecase.DeleteTaskUseCase
import com.example.domain.usecase.GetTaskByIdUseCase
import com.example.domain.usecase.GetTasksUseCase
import com.example.domain.usecase.SyncTasksUseCase
import com.example.domain.usecase.UpdateTaskUseCase

class AppContainer(private val context: Context) {
    val databaseModule = DatabaseModule(context)
    val networkModule = NetworkModule
    val repositoryModule = RepositoryModule(databaseModule.taskDao, networkModule.taskApi)

    val getTasksUseCase by lazy { GetTasksUseCase(repositoryModule.taskRepository) }
    val getTaskByIdUseCase by lazy { GetTaskByIdUseCase(repositoryModule.taskRepository) }
    val addTaskUseCase by lazy { AddTaskUseCase(repositoryModule.taskRepository) }
    val updateTaskUseCase by lazy { UpdateTaskUseCase(repositoryModule.taskRepository) }
    val deleteTaskUseCase by lazy { DeleteTaskUseCase(repositoryModule.taskRepository) }
    val syncTasksUseCase by lazy { SyncTasksUseCase(repositoryModule.taskRepository) }
}
