package com.example.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.di.AppContainer
import com.example.presentation.addedit.AddEditTaskViewModel
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.taskdetails.TaskDetailsViewModel
import com.example.presentation.tasklist.TaskListViewModel

class ViewModelFactory(
    private val container: AppContainer,
    private val extraArg: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(container.getTasksUseCase, container.syncTasksUseCase) as T
            }
            modelClass.isAssignableFrom(TaskListViewModel::class.java) -> {
                TaskListViewModel(
                    container.getTasksUseCase,
                    container.updateTaskUseCase,
                    container.syncTasksUseCase
                ) as T
            }
            modelClass.isAssignableFrom(TaskDetailsViewModel::class.java) -> {
                TaskDetailsViewModel(
                    getTaskByIdUseCase = container.getTaskByIdUseCase,
                    updateTaskUseCase = container.updateTaskUseCase,
                    deleteTaskUseCase = container.deleteTaskUseCase,
                    taskId = extraArg ?: ""
                ) as T
            }
            modelClass.isAssignableFrom(AddEditTaskViewModel::class.java) -> {
                AddEditTaskViewModel(
                    getTaskByIdUseCase = container.getTaskByIdUseCase,
                    addTaskUseCase = container.addTaskUseCase,
                    updateTaskUseCase = container.updateTaskUseCase,
                    taskId = extraArg
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        fun provideFactory(container: AppContainer, extraArg: String? = null): ViewModelProvider.Factory {
            return ViewModelFactory(container, extraArg)
        }
    }
}
