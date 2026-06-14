package com.example.presentation.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.domain.usecase.AddTaskUseCase
import com.example.domain.usecase.GetTaskByIdUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AddEditTaskViewModel(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val taskId: String?
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditTaskUiState())
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (taskId != null) {
            loadTask(taskId)
        }
    }

    private fun loadTask(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val task = getTaskByIdUseCase(id)
            if (task != null) {
                _uiState.value = AddEditTaskUiState(
                    title = task.title,
                    description = task.description,
                    priority = task.priority,
                    dueDate = task.dueDate,
                    isEditMode = true,
                    isLoading = false
                )
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load task") }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun onDescriptionChanged(desc: String) {
        _uiState.update { it.copy(description = desc) }
    }

    fun onPriorityChanged(priority: Priority) {
        _uiState.update { it.copy(priority = priority) }
    }

    fun onDueDateChanged(dueDate: Long) {
        _uiState.update { it.copy(dueDate = dueDate) }
    }

    fun saveTask() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Title cannot be blank") }
            return
        }

        viewModelScope.launch {
            if (state.isEditMode && taskId != null) {
                val existing = getTaskByIdUseCase(taskId)
                val updatedTask = Task(
                    id = taskId,
                    title = state.title.trim(),
                    description = state.description.trim(),
                    priority = state.priority,
                    dueDate = state.dueDate,
                    isCompleted = existing?.isCompleted ?: false,
                    isLocal = existing?.isLocal ?: true
                )
                updateTaskUseCase(updatedTask)
                _eventFlow.emit(UiEvent.SaveSuccess("Task updated successfully"))
            } else {
                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    title = state.title.trim(),
                    description = state.description.trim(),
                    priority = state.priority,
                    dueDate = state.dueDate,
                    isCompleted = false,
                    isLocal = true
                )
                addTaskUseCase(newTask)
                _eventFlow.emit(UiEvent.SaveSuccess("Task added successfully"))
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    sealed interface UiEvent {
        data class SaveSuccess(val message: String) : UiEvent
    }
}
