package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.CustomerPaymentDao
import com.ostadkar.app.data.local.dao.WorkerPaymentDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.CustomerPayment
import com.ostadkar.app.domain.model.WorkerPayment
import com.ostadkar.app.domain.repository.CustomerPaymentRepository
import com.ostadkar.app.domain.repository.WorkerPaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerPaymentRepositoryImpl @Inject constructor(
    private val dao: CustomerPaymentDao
) : CustomerPaymentRepository {
    override fun observeByProject(projectId: Long): Flow<List<CustomerPayment>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun observeProjectTotal(projectId: Long): Flow<Long> =
        dao.observeProjectTotal(projectId)

    override suspend fun save(payment: CustomerPayment): Long = dao.insert(payment.toEntity())

    override suspend fun delete(id: Long) {
        // Implement if needed via query
    }
}

@Singleton
class WorkerPaymentRepositoryImpl @Inject constructor(
    private val dao: WorkerPaymentDao
) : WorkerPaymentRepository {
    override fun observeByProject(projectId: Long): Flow<List<WorkerPayment>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun observeByProjectAndWorker(projectId: Long, workerId: Long): Flow<List<WorkerPayment>> =
        dao.observeByProjectAndWorker(projectId, workerId).map { list -> list.map { it.toDomain() } }

    override fun observeProjectTotal(projectId: Long): Flow<Long> =
        dao.observeProjectTotal(projectId)

    override suspend fun save(payment: WorkerPayment): Long = dao.insert(payment.toEntity())

    override suspend fun delete(id: Long) {
        // Implement if needed
    }
}
