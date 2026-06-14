package com.example.presentation.addedit

import com.example.domain.model.Priority

data class AddEditTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long = System.currentTimeMillis() + 24 * 60 * 60 * 1000, // default tomorrow
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
