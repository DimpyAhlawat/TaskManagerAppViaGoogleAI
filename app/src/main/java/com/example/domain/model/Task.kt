package com.example.domain.model

import java.io.Serializable

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val priority: Priority,
    val dueDate: Long,
    val isCompleted: Boolean,
    val isLocal: Boolean = false
) : Serializable
