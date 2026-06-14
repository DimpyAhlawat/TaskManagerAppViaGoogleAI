package com.example.presentation.taskdetails

import com.example.domain.model.Task

sealed interface TaskDetailsUiState {
    object Loading : TaskDetailsUiState
    data class Success(val task: Task) : TaskDetailsUiState
    data class Error(val message: String) : TaskDetailsUiState
}
