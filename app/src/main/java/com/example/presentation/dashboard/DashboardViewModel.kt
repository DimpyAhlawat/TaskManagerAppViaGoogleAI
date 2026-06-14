package com.example.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Priority
import com.example.domain.usecase.GetTasksUseCase
import com.example.domain.usecase.SyncTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val syncTasksUseCase: SyncTasksUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val uiState: StateFlow<DashboardUiState> = getTasksUseCase()
        .combine(_isRefreshing) { tasks, refreshing ->
            val total = tasks.size
            val completed = tasks.count { it.isCompleted }
            val pending = total - completed
            val highPriority = tasks.count { it.priority == Priority.HIGH && !it.isCompleted }

            DashboardUiState(
                totalTasks = total,
                completedTasks = completed,
                pendingTasks = pending,
                highPriorityTasks = highPriority,
                isLoading = false,
                isRefreshing = refreshing,
                error = null
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(isLoading = true)
        )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            val result = syncTasksUseCase()
            result.onFailure {
                _error.value = it.message ?: "Failed to synchronize network tasks"
            }
            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
