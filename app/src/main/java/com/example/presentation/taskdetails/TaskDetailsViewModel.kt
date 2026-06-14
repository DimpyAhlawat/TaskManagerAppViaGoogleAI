package com.example.presentation.taskdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.DeleteTaskUseCase
import com.example.domain.usecase.GetTaskByIdUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskDetailsViewModel(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val taskId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskDetailsUiState>(TaskDetailsUiState.Loading)
    val uiState: StateFlow<TaskDetailsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            _uiState.value = TaskDetailsUiState.Loading
            val task = getTaskByIdUseCase(taskId)
            if (task != null) {
                _uiState.value = TaskDetailsUiState.Success(task)
            } else {
                _uiState.value = TaskDetailsUiState.Error("Task not found")
            }
        }
    }

    fun toggleCompletion() {
        val currentState = _uiState.value
        if (currentState is TaskDetailsUiState.Success) {
            viewModelScope.launch {
                val updated = currentState.task.copy(isCompleted = !currentState.task.isCompleted)
                updateTaskUseCase(updated)
                _uiState.value = TaskDetailsUiState.Success(updated)
                _eventFlow.emit(UiEvent.ShowSnackbar(if (updated.isCompleted) "Task completed!" else "Task marked pending!"))
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
            _eventFlow.emit(UiEvent.NavigateBack)
        }
    }

    sealed interface UiEvent {
        data class ShowSnackbar(val message: String) : UiEvent
        object NavigateBack : UiEvent
    }
}
