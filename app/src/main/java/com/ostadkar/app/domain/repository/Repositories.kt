package com.ostadkar.app.domain.repository

import com.ostadkar.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun observeAll(): Flow<List<Customer>>
    fun observeById(id: Long): Flow<Customer?>
    fun search(query: String): Flow<List<Customer>>
    suspend fun getById(id: Long): Customer?
    suspend fun save(customer: Customer): Long
    suspend fun delete(id: Long)
}

interface ProjectRepository {
    fun observeAll(): Flow<List<Project>>
    fun observeActive(): Flow<List<Project>>
    fun observeById(id: Long): Flow<Project?>
    fun observeByCustomer(customerId: Long): Flow<List<Project>>
    fun observeActiveCount(): Flow<Int>
    suspend fun getById(id: Long): Project?
    suspend fun save(project: Project): Long
    suspend fun delete(id: Long)
}

interface WorkTypeRepository {
    fun observeAllActive(): Flow<List<WorkType>>
    suspend fun getById(id: Long): WorkType?
    suspend fun addCustom(nameFa: String): Long
}

interface WorkLocationRepository {
    fun observeByWorkType(workTypeId: Long): Flow<List<WorkLocation>>
    suspend fun addCustom(workTypeId: Long, nameFa: String): Long
}

interface WorkItemRepository {
    fun observeByProject(projectId: Long): Flow<List<WorkItem>>
    fun observeProjectTotal(projectId: Long): Flow<Long>
    suspend fun save(item: WorkItem): Long
    suspend fun delete(id: Long)
}

interface WorkerRepository {
    fun observeAllActive(): Flow<List<Worker>>
    fun observeById(id: Long): Flow<Worker?>
    suspend fun getById(id: Long): Worker?
    suspend fun save(worker: Worker): Long
    suspend fun delete(id: Long)
}

interface ProjectWorkerRepository {
    fun observeByProject(projectId: Long): Flow<List<ProjectWorker>>
    suspend fun assign(projectId: Long, workerId: Long, dailyWage: Long?): Long
    suspend fun remove(projectId: Long, workerId: Long)
}

interface DailyWorkRepository {
    fun observeByProject(projectId: Long): Flow<List<DailyWork>>
    fun observeByProjectAndWorker(projectId: Long, workerId: Long): Flow<List<DailyWork>>
    suspend fun save(dailyWork: DailyWork): Long
    suspend fun delete(id: Long)
}

interface OvertimeRepository {
    fun observeByDailyWork(dailyWorkId: Long): Flow<List<Overtime>>
    suspend fun save(overtime: Overtime): Long
    suspend fun delete(id: Long)
}

interface CustomerPaymentRepository {
    fun observeByProject(projectId: Long): Flow<List<CustomerPayment>>
    fun observeProjectTotal(projectId: Long): Flow<Long>
    suspend fun save(payment: CustomerPayment): Long
    suspend fun delete(id: Long)
}

interface WorkerPaymentRepository {
    fun observeByProject(projectId: Long): Flow<List<WorkerPayment>>
    fun observeByProjectAndWorker(projectId: Long, workerId: Long): Flow<List<WorkerPayment>>
    fun observeProjectTotal(projectId: Long): Flow<Long>
    suspend fun save(payment: WorkerPayment): Long
    suspend fun delete(id: Long)
}

interface ProjectExpenseRepository {
    fun observeByProject(projectId: Long): Flow<List<ProjectExpense>>
    fun observeProjectTotal(projectId: Long): Flow<Long>
    suspend fun save(expense: ProjectExpense): Long
    suspend fun delete(id: Long)
}

interface ProjectStageRepository {
    fun observeByProject(projectId: Long): Flow<List<ProjectStage>>
    suspend fun save(stage: ProjectStage): Long
    suspend fun seedDefaultStages(projectId: Long)
}

interface DashboardRepository {
    fun observeHomeDashboard(): Flow<HomeDashboard>
    fun observeProjectFinancial(projectId: Long): Flow<FinancialSummary>
}
