package com.example

import com.example.data.database.TaskDao
import com.example.data.database.TaskEntity
import com.example.data.network.TaskApi
import com.example.data.network.dto.TodoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import retrofit2.Response

class FakeTaskDao : TaskDao {
    val tasksMap = mutableMapOf<String, TaskEntity>()
    val tasksFlowState = MutableStateFlow<Map<String, TaskEntity>>(emptyMap())

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return tasksFlowState.map { it.values.toList().sortedBy { task -> task.dueDate } }
    }

    override suspend fun getTaskById(id: String): TaskEntity? {
        return tasksMap[id]
    }

    override suspend fun insertTask(task: TaskEntity) {
        tasksMap[task.id] = task
        tasksFlowState.value = tasksMap.toMap()
    }

    override suspend fun insertTasks(tasks: List<TaskEntity>) {
        tasks.forEach { tasksMap[it.id] = it }
        tasksFlowState.value = tasksMap.toMap()
    }

    override suspend fun updateTask(task: TaskEntity) {
        tasksMap[task.id] = task
        tasksFlowState.value = tasksMap.toMap()
    }

    override suspend fun deleteTaskById(id: String) {
        tasksMap.remove(id)
        tasksFlowState.value = tasksMap.toMap()
    }

    override suspend fun clearRemoteTasks() {
        val keysToRemove = tasksMap.filter { !it.value.isLocal }.keys
        keysToRemove.forEach { tasksMap.remove(it) }
        tasksFlowState.value = tasksMap.toMap()
    }
}

class FakeTaskApi(var response: Response<TodoResponse>) : TaskApi {
    override suspend fun getTodos(): Response<TodoResponse> {
        return response
    }
}
