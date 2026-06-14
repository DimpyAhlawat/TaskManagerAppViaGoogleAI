package com.example.data.repository

import com.example.data.database.TaskDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.data.network.NetworkResult
import com.example.data.network.TaskApi
import com.example.data.network.safeApiCall
import com.example.domain.model.Task
import com.example.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val taskApi: TaskApi
) : TaskRepository {

    override fun getTasksFlow(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun fetchTasksFromNetwork(): Result<Unit> {
        val result = safeApiCall { taskApi.getTodos() }
        return when (result) {
            is NetworkResult.Success -> {
                val remoteEntities = result.data.todos.map { it.toEntity() }
                taskDao.clearRemoteTasks()
                taskDao.insertTasks(remoteEntities)
                Result.success(Unit)
            }
            is NetworkResult.Error -> {
                Result.failure(Exception("API Error ${result.code}: ${result.message}"))
            }
            is NetworkResult.Exception -> {
                Result.failure(result.exception)
            }
        }
    }

    override suspend fun getTaskById(id: String): Task? {
        return taskDao.getTaskById(id)?.toDomain()
    }

    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteTaskById(id)
    }
}
