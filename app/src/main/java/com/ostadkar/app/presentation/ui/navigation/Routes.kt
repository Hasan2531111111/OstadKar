package com.ostadkar.app.presentation.ui.navigation

object Routes {
    const val HOME = "home"
    const val PROJECTS = "projects"
    const val PROJECT_DETAIL = "project/{projectId}"
    const val NEW_PROJECT = "new_project"
    const val CUSTOMERS = "customers"
    const val CUSTOMER_DETAIL = "customer/{customerId}"
    const val WORKERS = "workers"
    const val WORKER_DETAIL = "worker/{workerId}"
    const val PAYMENTS = "payments"
    const val REPORTS = "reports"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"

    // Project sub-screens
    const val PROJECT_WORK_ITEMS = "project/{projectId}/work_items"
    const val PROJECT_ADD_WORK = "project/{projectId}/add_work"
    const val PROJECT_PAYMENTS = "project/{projectId}/payments"
    const val PROJECT_WORKERS = "project/{projectId}/workers"
    const val PROJECT_STAGES = "project/{projectId}/stages"
    const val PROJECT_EXPENSES = "project/{projectId}/expenses"

    fun projectDetail(projectId: Long) = "project/$projectId"
    fun customerDetail(customerId: Long) = "customer/$customerId"
    fun workerDetail(workerId: Long) = "worker/$workerId"
    fun projectWorkItems(projectId: Long) = "project/$projectId/work_items"
    fun projectAddWork(projectId: Long) = "project/$projectId/add_work"
    fun projectPayments(projectId: Long) = "project/$projectId/payments"
    fun projectWorkers(projectId: Long) = "project/$projectId/workers"
    fun projectStages(projectId: Long) = "project/$projectId/stages"
    fun projectExpenses(projectId: Long) = "project/$projectId/expenses"
}
