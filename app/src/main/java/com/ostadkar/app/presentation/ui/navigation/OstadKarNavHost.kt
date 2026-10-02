package com.ostadkar.app.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ostadkar.app.presentation.ui.screens.customer.CustomersScreen
import com.ostadkar.app.presentation.ui.screens.home.HomeScreen
import com.ostadkar.app.presentation.ui.screens.payment.ProjectPaymentsScreen
import com.ostadkar.app.presentation.ui.screens.project.AddWorkScreen
import com.ostadkar.app.presentation.ui.screens.project.NewProjectScreen
import com.ostadkar.app.presentation.ui.screens.project.ProjectDetailScreen
import com.ostadkar.app.presentation.ui.screens.project.ProjectExpensesScreen
import com.ostadkar.app.presentation.ui.screens.project.ProjectStagesScreen
import com.ostadkar.app.presentation.ui.screens.project.ProjectsScreen
import com.ostadkar.app.presentation.ui.screens.project.WorkItemsScreen
import com.ostadkar.app.presentation.ui.screens.report.ReportsScreen
import com.ostadkar.app.presentation.ui.screens.settings.SettingsScreen
import com.ostadkar.app.presentation.ui.screens.worker.ProjectWorkersScreen
import com.ostadkar.app.presentation.ui.screens.worker.WorkersScreen


@Composable
fun OstadKarNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToProjects = { navController.navigate(Routes.PROJECTS) },
                onNavigateToCustomers = { navController.navigate(Routes.CUSTOMERS) },
                onNavigateToWorkers = { navController.navigate(Routes.WORKERS) },
                onNavigateToPayments = { navController.navigate(Routes.PAYMENTS) },
                onNavigateToReports = { navController.navigate(Routes.REPORTS) },
                onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                onNewProject = { navController.navigate(Routes.NEW_PROJECT) },
                onProjectClick = { id -> navController.navigate(Routes.projectDetail(id)) }
            )
        }

        composable(Routes.PROJECTS) {
            ProjectsScreen(
                onBack = { navController.popBackStack() },
                onProjectClick = { id -> navController.navigate(Routes.projectDetail(id)) },
                onNewProject = { navController.navigate(Routes.NEW_PROJECT) }
            )
        }

        composable(
            route = Routes.PROJECT_DETAIL,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            ProjectDetailScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onNavigateToWorkItems = { navController.navigate(Routes.projectWorkItems(projectId)) },
                onNavigateToPayments = { navController.navigate(Routes.projectPayments(projectId)) },
                onNavigateToWorkers = { navController.navigate(Routes.projectWorkers(projectId)) },
                onNavigateToStages = { navController.navigate(Routes.projectStages(projectId)) },
                onNavigateToExpenses = { navController.navigate(Routes.projectExpenses(projectId)) }
            )
        }

        composable(
            route = Routes.PROJECT_WORK_ITEMS,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val projectId = backStackEntry.arguments?.getLong("projectId") ?: 0L
            WorkItemsScreen(
                onBack = { navController.popBackStack() },
                onAddWork = { navController.navigate(Routes.projectAddWork(projectId)) }
            )
        }

        composable(
            route = Routes.PROJECT_ADD_WORK,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) {
            AddWorkScreen(
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PROJECT_PAYMENTS,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) {
            ProjectPaymentsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PROJECT_WORKERS,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) {
            ProjectWorkersScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PROJECT_STAGES,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) {
            ProjectStagesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PROJECT_EXPENSES,
            arguments = listOf(navArgument("projectId") { type = NavType.LongType })
        ) {
            ProjectExpensesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.NEW_PROJECT) {

            NewProjectScreen(
                onBack = { navController.popBackStack() },
                onCreated = { id ->
                    navController.popBackStack()
                    navController.navigate(Routes.projectDetail(id))
                }
            )
        }

        composable(Routes.CUSTOMERS) {
            CustomersScreen(
                onBack = { navController.popBackStack() },
                onCustomerClick = { id -> navController.navigate(Routes.customerDetail(id)) }
            )
        }

        composable(Routes.WORKERS) {
            WorkersScreen(
                onBack = { navController.popBackStack() },
                onWorkerClick = { id -> navController.navigate(Routes.workerDetail(id)) }
            )
        }

        composable(Routes.REPORTS) {
            ReportsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
