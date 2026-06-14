package com.example.presentation.navigation

object Routes {
    const val DASHBOARD = "dashboard"
    const val TASK_LIST = "taskList?priority={priority}&isCompleted={isCompleted}"
    const val ADD_TASK = "addTask"
    const val EDIT_TASK = "editTask/{taskId}"
    const val TASK_DETAILS = "taskDetails/{taskId}"

    fun taskListWithFilters(priority: String? = null, isCompleted: String? = null): String {
        return "taskList?priority=${priority ?: ""}&isCompleted=${isCompleted ?: ""}"
    }

    fun editTask(taskId: String): String {
        return "editTask/$taskId"
    }

    fun taskDetails(taskId: String): String {
        return "taskDetails/$taskId"
    }
}
