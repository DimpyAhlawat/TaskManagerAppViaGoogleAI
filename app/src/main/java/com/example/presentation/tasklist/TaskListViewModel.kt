package com.example.presentation.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.domain.usecase.GetTasksUseCase
import com.example.domain.usecase.SyncTasksUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val syncTasksUseCase: SyncTasksUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    val selectedPriority = _selectedPriority.asStateFlow()

    private val _selectedStatus = MutableStateFlow<Boolean?>(null)
    val selectedStatus = _selectedStatus.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<TaskListUiState> = combine(
        _searchQuery,
        _selectedPriority,
        _selectedStatus,
        _isRefreshing,
        _error
    ) { query, priority, status, refreshing, err ->
        Tuple5(query, priority, status, refreshing, err)
    }.flatMapLatest { tuple ->
        getTasksUseCase(tuple.query, tuple.priority, tuple.status).map { filteredTasks ->
            TaskListUiState(
                tasks = filteredTasks,
                isLoading = false,
                searchQuery = tuple.query,
                selectedPriorityFilter = tuple.priority,
                selectedStatusFilter = tuple.status,
                isRefreshing = tuple.refreshing,
                error = tuple.err
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskListUiState(isLoading = true)
    )

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onPriorityFilterChanged(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun onStatusFilterChanged(isCompleted: Boolean?) {
        _selectedStatus.value = isCompleted
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updated = task.copy(isCompleted = !task.isCompleted)
            updateTaskUseCase(updated)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _error.value = null
            val result = syncTasksUseCase()
            result.onFailure {
                _error.value = it.message ?: "Failed to refresh tasks from DummyJson"
            }
            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    private class Tuple5<A, B, C, D, E>(
        val query: A,
        val priority: B,
        val status: C,
        val refreshing: D,
        val err: E
    )
}
