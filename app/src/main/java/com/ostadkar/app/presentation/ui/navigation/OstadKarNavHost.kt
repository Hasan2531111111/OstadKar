package com.ostadkar.app.presentation.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ostadkar.app.presentation.ui.screens.customer.CustomersScreen
import com.ostadkar.app.presentation.ui.screens.home.HomeScreen
import com.ostadkar.app.presentation.ui.screens.payment.PaymentsHubScreen
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
import com.ostadkar.app.presentation.ui.screens.settings.ProfileScreen
import com.ostadkar.app.presentation.ui.screens.worker.ProjectWorkersScreen
import com.ostadkar.app.presentation.ui.screens.worker.WorkersScreen
import com.ostadkar.app.presentation.ui.theme.Primary

private data class BottomItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomItems = listOf(
    BottomItem(Routes.HOME, "خانه", Icons.Filled.Home, Icons.Outlined.Home),
    BottomItem(Routes.PROJECTS, "پروژه", Icons.Filled.Work, Icons.Outlined.Work),
    BottomItem(Routes.REPORTS, "گزارش", Icons.Filled.Assessment, Icons.Outlined.Assessment),
    BottomItem(Routes.SETTINGS, "بیشتر", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
)

@Composable
fun OstadKarNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = Primary.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToProjects = { navController.navigate(Routes.PROJECTS) },
                    onNavigateToCustomers = { navController.navigate(Routes.CUSTOMERS) },
                    onNavigateToWorkers = { navController.navigate(Routes.WORKERS) },
                    onNavigateToPayments = { navController.navigate(Routes.PAYMENTS) },
                    onNavigateToReports = { navController.navigate(Routes.REPORTS) },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
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

            composable(Routes.PAYMENTS) {
                PaymentsHubScreen(
                    onBack = { navController.popBackStack() },
                    onProjectPayments = { id -> navController.navigate(Routes.projectPayments(id)) }
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
                    onCustomerClick = { /* detail later */ }
                )
            }

            composable(Routes.WORKERS) {
                WorkersScreen(
                    onBack = { navController.popBackStack() },
                    onWorkerClick = { /* detail later */ }
                )
            }

            composable(Routes.REPORTS) {
                ReportsScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onEditProfile = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
