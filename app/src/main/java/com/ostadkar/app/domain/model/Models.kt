package com.ostadkar.app.domain.model

/**
 * Domain models – pure Kotlin, no Android/Room dependencies.
 */

data class Customer(
    val id: Long = 0,
    val name: String,
    val mobile: String = "",
    val address: String = "",
    val notes: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)

data class Project(
    val id: Long = 0,
    val customerId: Long,
    val title: String = "",
    val address: String = "",
    val description: String = "",
    val status: String = "active",
    val startDate: Long? = null,
    val endDate: Long? = null,
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    // Denormalized for UI convenience
    val customerName: String = ""
)

data class WorkType(
    val id: Long = 0,
    val code: String,
    val nameFa: String,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)

data class WorkLocation(
    val id: Long = 0,
    val workTypeId: Long,
    val code: String,
    val nameFa: String,
    val isCustom: Boolean = false,
    val sortOrder: Int = 0
)

data class WorkItem(
    val id: Long = 0,
    val projectId: Long,
    val workTypeId: Long?,
    val workTypeName: String,
    val workLocationId: Long?,
    val workLocationName: String,
    val unit: String = "square_meter",
    val unitCustomName: String? = null,
    val length: Double? = null,
    val width: Double? = null,
    val height: Double? = null,
    val quantity: Double = 0.0,
    val unitPrice: Long = 0,
    val totalPrice: Long = 0,
    val notes: String = "",
    val sortOrder: Int = 0
)

data class Worker(
    val id: Long = 0,
    val name: String,
    val mobile: String = "",
    val type: String = "worker", // master | worker
    val specialty: String = "",
    val defaultDailyWage: Long = 0,
    val notes: String = "",
    val isActive: Boolean = true
)

data class ProjectWorker(
    val id: Long = 0,
    val projectId: Long,
    val workerId: Long,
    val dailyWage: Long? = null,
    val notes: String = "",
    val workerName: String = "",
    val workerType: String = ""
)

data class DailyWork(
    val id: Long = 0,
    val projectId: Long,
    val workerId: Long,
    val workDate: Long,
    val workType: String = "full_day",
    val hours: Double = 0.0,
    val baseWage: Long = 0,
    val notes: String = ""
)

data class Overtime(
    val id: Long = 0,
    val dailyWorkId: Long,
    val hours: Double = 0.0,
    val ratePerHour: Long = 0,
    val totalAmount: Long = 0,
    val notes: String = ""
)

data class CustomerPayment(
    val id: Long = 0,
    val projectId: Long,
    val amount: Long,
    val paymentDate: Long,
    val method: String = "cash",
    val notes: String = ""
)

data class WorkerPayment(
    val id: Long = 0,
    val projectId: Long,
    val workerId: Long,
    val amount: Long,
    val paymentDate: Long,
    val paymentType: String = "partial",
    val method: String = "cash",
    val notes: String = ""
)

data class ProjectExpense(
    val id: Long = 0,
    val projectId: Long,
    val category: String = "other",
    val title: String,
    val amount: Long,
    val expenseDate: Long,
    val notes: String = ""
)

data class ProjectStage(
    val id: Long = 0,
    val projectId: Long,
    val name: String,
    val sortOrder: Int = 0,
    val status: String = "not_started",
    val completedAt: Long? = null,
    val notes: String = ""
)

/** Aggregated financial summary for a project or the whole app */
data class FinancialSummary(
    val totalIncome: Long = 0,          // sum of work items
    val receivedFromCustomers: Long = 0,
    val receivable: Long = 0,           // income - received
    val workerWages: Long = 0,          // base + overtime
    val paidToWorkers: Long = 0,
    val payableToWorkers: Long = 0,     // wages - paid
    val materialsAndOther: Long = 0,
    val totalExpenses: Long = 0,        // wages + materials + other
    val approximateProfit: Long = 0     // income - totalExpenses
)

/** Dashboard cards on home screen */
data class HomeDashboard(
    val activeProjectsCount: Int = 0,
    val receivableFromCustomers: Long = 0,
    val payableToWorkers: Long = 0,
    val totalIncome: Long = 0,
    val totalExpenses: Long = 0,
    val approximateProfit: Long = 0
)
