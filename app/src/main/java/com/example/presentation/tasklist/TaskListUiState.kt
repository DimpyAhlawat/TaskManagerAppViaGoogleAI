package com.example.presentation.tasklist

import com.example.domain.model.Priority
import com.example.domain.model.Task

data class TaskListUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val selectedPriorityFilter: Priority? = null,
    val selectedStatusFilter: Boolean? = null, // null = All, false = Pending, true = Completed
    val error: String? = null,
    val isRefreshing: Boolean = false
)
