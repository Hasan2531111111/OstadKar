package com.ostadkar.app.data.repository

import com.ostadkar.app.data.local.dao.DailyWorkDao
import com.ostadkar.app.data.mapper.toDomain
import com.ostadkar.app.data.mapper.toEntity
import com.ostadkar.app.domain.model.DailyWork
import com.ostadkar.app.domain.repository.DailyWorkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyWorkRepositoryImpl @Inject constructor(
    private val dao: DailyWorkDao
) : DailyWorkRepository {
    override fun observeByProject(projectId: Long): Flow<List<DailyWork>> =
        dao.observeByProject(projectId).map { list -> list.map { it.toDomain() } }

    override fun observeByProjectAndWorker(projectId: Long, workerId: Long): Flow<List<DailyWork>> =
        dao.observeByProjectAndWorker(projectId, workerId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(dailyWork: DailyWork): Long = dao.insert(dailyWork.toEntity())

    override suspend fun delete(id: Long) {
        dao.getById(id)?.let { dao.delete(it) }
    }
}
