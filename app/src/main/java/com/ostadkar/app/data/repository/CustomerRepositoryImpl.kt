package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.CustomerDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.Customer
import com.ostadkar.app.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val dao: CustomerDao
) : CustomerRepository {
    override fun observeAll(): Flow<List<Customer>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Customer?> =
        dao.observeById(id).map { it?.toDomain() }

    override fun search(query: String): Flow<List<Customer>> =
        dao.search(query).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Customer? = dao.getById(id)?.toDomain()

    override suspend fun save(customer: Customer): Long = dao.insert(customer.toEntity())

    override suspend fun delete(id: Long) = dao.deleteById(id)
}
