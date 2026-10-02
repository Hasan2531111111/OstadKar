package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.WorkerDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.Worker
import com.ostadkar.app.domain.repository.WorkerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerRepositoryImpl @Inject constructor(
    private val dao: WorkerDao
) : WorkerRepository {
    override fun observeAllActive(): Flow<List<Worker>> =
        dao.observeAllActive().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Worker?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): Worker? = dao.getById(id)?.toDomain()

    override suspend fun save(worker: Worker): Long = dao.insert(worker.toEntity())

    override suspend fun delete(id: Long) {
        dao.getById(id)?.let { dao.delete(it) }
    }
}
