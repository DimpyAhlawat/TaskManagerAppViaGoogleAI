package com.example.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.TaskManagerApplication
import com.example.domain.model.Priority
import com.example.presentation.ViewModelFactory
import com.example.presentation.addedit.AddEditTaskScreen
import com.example.presentation.addedit.AddEditTaskViewModel
import com.example.presentation.dashboard.DashboardScreen
import com.example.presentation.dashboard.DashboardViewModel
import com.example.presentation.taskdetails.TaskDetailsScreen
import com.example.presentation.taskdetails.TaskDetailsViewModel
import com.example.presentation.tasklist.TaskListScreen
import com.example.presentation.tasklist.TaskListViewModel

@Composable
fun TaskNavGraph(
    navController: NavHostController,
    application: TaskManagerApplication,
    modifier: Modifier = Modifier
) {
    val container = application.container

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD,
        modifier = modifier
    ) {
        // --- Dashboard Screen ---
        composable(Routes.DASHBOARD) {
            val dbViewModel: DashboardViewModel = viewModel(
                factory = ViewModelFactory.provideFactory(container)
            )
            DashboardScreen(
                viewModel = dbViewModel,
                onNavigateToTaskList = { priority, isCompleted ->
                    val priorityStr = priority?.name
                    val completedStr = isCompleted?.toString()
                    navController.navigate(Routes.taskListWithFilters(priorityStr, completedStr))
                },
                onAddTask = {
                    navController.navigate(Routes.ADD_TASK)
                }
            )
        }

        // --- Task List Screen ---
        composable(
            route = Routes.TASK_LIST,
            arguments = listOf(
                navArgument("priority") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("isCompleted") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val priorityStr = backStackEntry.arguments?.getString("priority")
            val isCompletedStr = backStackEntry.arguments?.getString("isCompleted")

            val initialPriority = priorityStr?.takeIf { it.isNotEmpty() }?.let {
                try { Priority.valueOf(it) } catch (e: Exception) { null }
            }
            val initialStatus = isCompletedStr?.takeIf { it.isNotEmpty() }?.toBooleanStrictOrNull()

            val listViewModel: TaskListViewModel = viewModel(
                factory = ViewModelFactory.provideFactory(container)
            )

            TaskListScreen(
                viewModel = listViewModel,
                initialPriorityFilter = initialPriority,
                initialStatusFilter = initialStatus,
                onNavigateToDetails = { taskId ->
                    navController.navigate(Routes.taskDetails(taskId))
                },
                onAddTask = {
                    navController.navigate(Routes.ADD_TASK)
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- Add Task Screen ---
        composable(Routes.ADD_TASK) {
            val addViewModel: AddEditTaskViewModel = viewModel(
                factory = ViewModelFactory.provideFactory(container, null)
            )
            AddEditTaskScreen(
                viewModel = addViewModel,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- Edit Task Screen ---
        composable(
            route = Routes.EDIT_TASK,
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val editViewModel: AddEditTaskViewModel = viewModel(
                factory = ViewModelFactory.provideFactory(container, taskId)
            )
            AddEditTaskScreen(
                viewModel = editViewModel,
                onSaveSuccess = {
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- Task Details Screen ---
        composable(
            route = Routes.TASK_DETAILS,
            arguments = listOf(
                navArgument("taskId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val detailsViewModel: TaskDetailsViewModel = viewModel(
                factory = ViewModelFactory.provideFactory(container, taskId)
            )
            TaskDetailsScreen(
                viewModel = detailsViewModel,
                onEditTask = { id ->
                    navController.navigate(Routes.editTask(id))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
