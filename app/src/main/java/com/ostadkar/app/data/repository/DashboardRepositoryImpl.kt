package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.*
import com.ostadkar.app.domain.model.FinancialSummary
import com.ostadkar.app.domain.model.HomeDashboard
import com.ostadkar.app.domain.repository.DashboardRepository
import com.ostadkar.app.domain.usecase.ProfitCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val projectDao: ProjectDao,
    private val workItemDao: WorkItemDao,
    private val customerPaymentDao: CustomerPaymentDao,
    private val dailyWorkDao: DailyWorkDao,
    private val overtimeDao: OvertimeDao,
    private val workerPaymentDao: WorkerPaymentDao,
    private val expenseDao: ProjectExpenseDao
) : DashboardRepository {

    override fun observeHomeDashboard(): Flow<HomeDashboard> {
        return combine(
            projectDao.observeActiveCount(),
            workItemDao.observeAllProjectsTotal(),
            customerPaymentDao.observeAllTotal(),
            workerPaymentDao.observeAllTotal()
        ) { activeCount, totalIncome, received, paidToWorkers ->
            val receivable = ProfitCalculator.receivable(totalIncome, received)
            HomeDashboard(
                activeProjectsCount = activeCount,
                receivableFromCustomers = receivable,
                payableToWorkers = 0L,
                totalIncome = totalIncome,
                totalExpenses = paidToWorkers,
                approximateProfit = totalIncome - paidToWorkers
            )
        }
    }

    override fun observeProjectFinancial(projectId: Long): Flow<FinancialSummary> {
        return combine(
            workItemDao.observeProjectTotal(projectId),
            customerPaymentDao.observeProjectTotal(projectId),
            dailyWorkDao.observeTotalBaseWage(projectId),
            overtimeDao.observeProjectOvertimeTotal(projectId),
            workerPaymentDao.observeProjectTotal(projectId),
            expenseDao.observeProjectTotal(projectId)
        ) { income, received, baseWage, overtime, paid, expenses ->
            val wages = baseWage + overtime
            val totalExpenses = wages + expenses
            FinancialSummary(
                totalIncome = income,
                receivedFromCustomers = received,
                receivable = ProfitCalculator.receivable(income, received),
                workerWages = wages,
                paidToWorkers = paid,
                payableToWorkers = ProfitCalculator.payable(wages, paid),
                materialsAndOther = expenses,
                totalExpenses = totalExpenses,
                approximateProfit = ProfitCalculator.approximateProfit(income, wages, expenses)
            )
        }
    }
}
