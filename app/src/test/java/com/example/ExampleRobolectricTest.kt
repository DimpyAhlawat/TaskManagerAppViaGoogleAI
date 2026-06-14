package com.example

import com.example.data.database.TaskEntity
import com.example.data.mapper.toDomain
import com.example.data.network.dto.TodoDto
import com.example.data.network.dto.TodoResponse
import com.example.data.repository.TaskRepositoryImpl
import com.example.domain.model.Priority
import com.example.domain.model.Task
import com.example.domain.usecase.GetTasksUseCase
import com.example.domain.usecase.SyncTasksUseCase
import com.example.domain.usecase.UpdateTaskUseCase
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.tasklist.TaskListViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var fakeDao: FakeTaskDao
    private lateinit var fakeApi: FakeTaskApi
    private lateinit var repository: TaskRepositoryImpl
    private lateinit var getTasksUseCase: GetTasksUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var syncTasksUseCase: SyncTasksUseCase

    @Before
    fun setUp() {
        fakeDao = FakeTaskDao()
        val emptyResponse = TodoResponse(
            todos = emptyList(),
            total = 0
        )
        fakeApi = FakeTaskApi(Response.success(emptyResponse))
        repository = TaskRepositoryImpl(fakeDao, fakeApi)
        getTasksUseCase = GetTasksUseCase(repository)
        updateTaskUseCase = UpdateTaskUseCase(repository)
        syncTasksUseCase = SyncTasksUseCase(repository)
    }

    @Test
    fun repository_getTasksFlow_emits_mapped_domain_models() = runTest {
        val entity = TaskEntity(
            id = "local_1",
            title = "Test Entity",
            description = "Test Desc",
            priority = "MEDIUM",
            dueDate = 1000L,
            isCompleted = false,
            isLocal = true
        )
        fakeDao.insertTask(entity)

        val emitedTasks = repository.getTasksFlow().first()
        assertEquals(1, emitedTasks.size)
        assertEquals("Test Entity", emitedTasks[0].title)
        assertEquals(Priority.MEDIUM, emitedTasks[0].priority)
    }

    @Test
    fun repository_fetchTasksFromNetwork_syncs_to_dao() = runTest {
        val todoResponse = TodoResponse(
            todos = listOf(
                TodoDto(id = 1, todo = "Mock Network Todo", completed = false, userId = 10)
            ),
            total = 1
        )
        fakeApi.response = Response.success(todoResponse)
        val result = repository.fetchTasksFromNetwork()
        assertTrue(result.isSuccess)

        val localTasks = fakeDao.getAllTasks().first()
        assertEquals(1, localTasks.size)
        assertEquals("Mock Network Todo", localTasks[0].title)
        assertEquals(false, localTasks[0].isLocal)
    }

    @Test
    fun dashboardViewModel_computes_statistics_correctly() = runTest {
        val task1 = Task("1", "T1", "D1", Priority.HIGH, 1000L, isCompleted = false, isLocal = true)
        val task2 = Task("2", "T2", "D2", Priority.LOW, 1000L, isCompleted = true, isLocal = true)

        repository.insertTask(task1)
        repository.insertTask(task2)

        val viewModel = DashboardViewModel(getTasksUseCase, syncTasksUseCase)
        val state = viewModel.uiState.first { !it.isLoading }

        assertEquals(2, state.totalTasks)
        assertEquals(1, state.completedTasks)
        assertEquals(1, state.pendingTasks)
        assertEquals(1, state.highPriorityTasks)
    }

    @Test
    fun taskListViewModel_toggles_task_correctly() = runTest {
        val task = Task("1", "T1", "D1", Priority.MEDIUM, 1000L, isCompleted = false, isLocal = true)
        repository.insertTask(task)

        val viewModel = TaskListViewModel(getTasksUseCase, updateTaskUseCase, syncTasksUseCase)
        viewModel.toggleTaskCompletion(task)

        val updatedTask = repository.getTaskById("1")
        assertNotNull(updatedTask)
        assertTrue(updatedTask!!.isCompleted)
    }
}
